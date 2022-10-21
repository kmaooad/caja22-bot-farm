package edu.kmaooad.web;

import edu.kmaooad.domain.AddMessage;
import edu.kmaooad.domain.AddMessageResult;
import edu.kmaooad.utils.TelegramWebhookProxy;
import edu.kmaooad.utils.TestExecutionContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.function.adapter.azure.FunctionInvoker;

public class TelegramWebhookHandlerTest {

  @Test
  public void shouldReturnErrorIfRequestDontHaveMessageId() {
    FunctionInvoker<AddMessage, AddMessageResult> handler =
        new FunctionInvoker<>(TelegramWebhookProxy.class);

    AddMessageResult result =
        handler.handleRequest(new AddMessage("test_message"), new TestExecutionContext());
    handler.close();

    Assertions.assertFalse(result.isSuccessful());
    Assertions.assertEquals("Can't get message_id from request", result.getErrorMessage());
  }

  @Test
  public void shouldReturnSuccessIfRequestHaveMessageId() {
    FunctionInvoker<AddMessage, AddMessageResult> handler =
        new FunctionInvoker<>(TelegramWebhookProxy.class);

    AddMessageResult result =
        handler.handleRequest(new AddMessage("\"message_id\": 1"), new TestExecutionContext());
    handler.close();

    Assertions.assertTrue(result.isSuccessful());
    Assertions.assertEquals("1", result.getResult());
  }
}
