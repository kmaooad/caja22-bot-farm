package edu.kmaooad.web.webhook;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import edu.kmaooad.web.response.ErrorResponse;
import java.util.Optional;
import org.springframework.cloud.function.adapter.azure.FunctionInvoker;

public class TelegramWebhookHandler extends FunctionInvoker<Optional<String>, Void> {

  @FunctionName("TelegramWebhook")
  public HttpResponseMessage run(
      @HttpTrigger(
              name = "req",
              methods = {HttpMethod.POST},
              authLevel = AuthorizationLevel.FUNCTION)
          HttpRequestMessage<Optional<String>> request,
      final ExecutionContext context) {
    try {
      this.handleRequest(request.getBody(), context);
      return request.createResponseBuilder(HttpStatus.OK).build();
    } catch (Exception ex) {
      return request
          .createResponseBuilder(HttpStatus.OK)
          .body(ErrorResponse.builder().message(ex.getMessage()).build())
          .build();
    }
  }
}
