package edu.kmaooad.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import edu.kmaooad.domain.AddMessageResult;
import java.util.Optional;
import org.springframework.cloud.function.adapter.azure.FunctionInvoker;
import org.telegram.telegrambots.meta.api.objects.Update;

public class TelegramWebhookHandler extends FunctionInvoker<Update, AddMessageResult> {

  private final ObjectMapper mapper = new ObjectMapper();

  @FunctionName("TelegramWebhook")
  public HttpResponseMessage run(
      @HttpTrigger(
              name = "req",
              methods = {HttpMethod.POST},
              authLevel = AuthorizationLevel.FUNCTION)
          HttpRequestMessage<Optional<String>> request,
      final ExecutionContext context) {
    final String message = request.getBody().orElse(null);
    if (message == null) {
      return request
          .createResponseBuilder(HttpStatus.BAD_REQUEST)
          .body("Request body can't be empty")
          .build();
    }

    try {
      final AddMessageResult addMessageResult =
          handleRequest(mapper.readValue(message, Update.class), context);
      if (addMessageResult.isSuccessful()) {
        return request
            .createResponseBuilder(HttpStatus.OK)
            .body(addMessageResult.getResult())
            .build();
      } else {
        return request
            .createResponseBuilder(HttpStatus.BAD_REQUEST)
            .body(addMessageResult.getErrorMessage())
            .build();
      }
    } catch (JsonProcessingException ex) {
      return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Json error").build();
    }
  }
}
