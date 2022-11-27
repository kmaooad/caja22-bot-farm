package edu.kmaooad.web;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.kmaooad.command.CommandDispatcher;
import edu.kmaooad.domain.mapper.UserRequestMapper;
import edu.kmaooad.domain.model.UserRequest;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.Update;

public class TelegramWebhookTest {

  @Test
  public void shouldHandleUpdate() throws Exception {
    final ObjectMapper objectMapper = mock(ObjectMapper.class);
    final UserRequestMapper userRequestMapper = mock(UserRequestMapper.class);
    final CommandDispatcher commandDispatcher = mock(CommandDispatcher.class);

    when(objectMapper.readValue(anyString(), eq(Update.class))).thenReturn(new Update());
    when(userRequestMapper.toUserRequest(any(Update.class)))
        .thenReturn(UserRequest.builder().build());

    final TelegramWebhook telegramWebhook =
        new TelegramWebhook(objectMapper, userRequestMapper, commandDispatcher);
    assertDoesNotThrow(() -> telegramWebhook.apply("Some string"));
  }
}
