package edu.kmaooad.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.kmaooad.domain.AddMessageResult;
import edu.kmaooad.domain.Message;
import edu.kmaooad.repository.MessageRepository;
import edu.kmaooad.telegram.TelegramBot;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.Update;

public class TelegramWebhookTest {

  @Test
  public void shouldReturnSuccessfulAddMessageResultIfMessageIsValid() throws Exception {
    final MessageRepository messageRepository = mock(MessageRepository.class);
    final TelegramBot telegramBot = mock(TelegramBot.class);
    final ObjectMapper objectMapper = mock(ObjectMapper.class);
    doReturn(Message.builder().message(Message.Content.builder().messageId(25L).build()).build())
        .when(objectMapper)
        .readValue(anyString(), eq(Message.class));

    final AddMessageResult expected = new AddMessageResult(true, "Success", null);

    final TelegramWebhook telegramWebhook = new TelegramWebhook(objectMapper, telegramBot);
    final AddMessageResult actual = telegramWebhook.apply(new Update());

    assertEquals(expected.isSuccessful(), actual.isSuccessful());
    assertEquals(expected.getResult(), actual.getResult());
    assertEquals(expected.getErrorMessage(), actual.getErrorMessage());
  }
}
