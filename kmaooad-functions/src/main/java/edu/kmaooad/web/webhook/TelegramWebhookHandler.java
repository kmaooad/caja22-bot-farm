package edu.kmaooad.web;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
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
      final String responseBody = String.format("{\"message\":\"%s\"}", ex.getMessage());
      return request.createResponseBuilder(HttpStatus.OK).body(responseBody).build();
    }
  }
}
