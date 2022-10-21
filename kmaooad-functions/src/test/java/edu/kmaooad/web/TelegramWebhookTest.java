package edu.kmaooad.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import edu.kmaooad.domain.AddMessage;
import edu.kmaooad.domain.AddMessageResult;
import edu.kmaooad.exception.InvalidMessageException;
import edu.kmaooad.parser.RequestParser;
import edu.kmaooad.repository.MessageRepository;
import org.junit.jupiter.api.Test;

public class TelegramWebhookTest {

  @Test
  public void shouldReturnSuccessfulAddMessageResultIfMessageIsValid() throws Exception {
    final MessageRepository messageRepository = mock(MessageRepository.class);
    final RequestParser requestParser = mock(RequestParser.class);
    doReturn(25).when(requestParser).getMessageId(any(String.class));

    final AddMessageResult expected = new AddMessageResult(true, "25", null);

    final TelegramWebhook telegramWebhook = new TelegramWebhook(requestParser, messageRepository);
    final AddMessageResult actual = telegramWebhook.apply(new AddMessage(""));

    assertEquals(expected.isSuccessful(), actual.isSuccessful());
    assertEquals(expected.getResult(), actual.getResult());
    assertEquals(expected.getErrorMessage(), actual.getErrorMessage());
  }

  @Test
  public void shouldReturnFailedAddMessageResultIfMessageIsInvalid() throws Exception {
    final MessageRepository messageRepository = mock(MessageRepository.class);
    final RequestParser requestParser = mock(RequestParser.class);
    doThrow(new InvalidMessageException("Exception message"))
        .when(requestParser)
        .getMessageId(any(String.class));

    final AddMessageResult expected = new AddMessageResult(false, null, "Exception message");

    final TelegramWebhook telegramWebhook = new TelegramWebhook(requestParser, messageRepository);
    final AddMessageResult actual = telegramWebhook.apply(new AddMessage(""));

    assertEquals(expected.isSuccessful(), actual.isSuccessful());
    assertEquals(expected.getResult(), actual.getResult());
    assertEquals(expected.getErrorMessage(), actual.getErrorMessage());
  }
}
