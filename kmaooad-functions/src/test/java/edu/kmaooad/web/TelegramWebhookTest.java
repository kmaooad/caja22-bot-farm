package edu.kmaooad.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.kmaooad.domain.AddMessage;
import edu.kmaooad.domain.AddMessageResult;
import edu.kmaooad.domain.Message;
import edu.kmaooad.repository.MessageRepository;
import org.junit.jupiter.api.Test;

public class TelegramWebhookTest {

  @Test
  public void shouldReturnSuccessfulAddMessageResultIfMessageIsValid() throws Exception {
    final MessageRepository messageRepository = mock(MessageRepository.class);
    final ObjectMapper objectMapper = mock(ObjectMapper.class);
    doReturn(Message.builder().message(Message.Content.builder().messageId(25L).build()).build())
        .when(objectMapper)
        .readValue(anyString(), eq(Message.class));

    final AddMessageResult expected = new AddMessageResult(true, "25", null);

    final TelegramWebhook telegramWebhook = new TelegramWebhook(objectMapper, messageRepository);
    final AddMessageResult actual = telegramWebhook.apply(new AddMessage(""));

    assertEquals(expected.isSuccessful(), actual.isSuccessful());
    assertEquals(expected.getResult(), actual.getResult());
    assertEquals(expected.getErrorMessage(), actual.getErrorMessage());
  }

  @Test
  public void shouldReturnFailedAddMessageResultIfMessageIsInvalid() throws Exception {
    final MessageRepository messageRepository = mock(MessageRepository.class);
    final ObjectMapper objectMapper = mock(ObjectMapper.class);
    doThrow(JsonProcessingException.class)
        .when(objectMapper)
        .readValue(anyString(), eq(Message.class));

    final AddMessageResult expected =
        new AddMessageResult(false, null, "Can't get message_id from request");

    final TelegramWebhook telegramWebhook = new TelegramWebhook(objectMapper, messageRepository);
    final AddMessageResult actual = telegramWebhook.apply(new AddMessage(""));

    assertEquals(expected.isSuccessful(), actual.isSuccessful());
    assertEquals(expected.getResult(), actual.getResult());
    assertEquals(expected.getErrorMessage(), actual.getErrorMessage());
  }
}
