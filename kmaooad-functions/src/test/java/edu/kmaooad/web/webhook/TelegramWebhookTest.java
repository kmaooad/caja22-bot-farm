package edu.kmaooad.web.webhook;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.kmaooad.command.dispatch.CommandDispatcher;
import edu.kmaooad.domain.mapper.UserRequestMapper;
import edu.kmaooad.exception.InvalidRequestBodyException;
import edu.kmaooad.web.request.UserRequest;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.Update;

public class TelegramWebhookTest {

  private ObjectMapper objectMapper;
  private UserRequestMapper userRequestMapper;
  private CommandDispatcher commandDispatcher;
  private TelegramWebhook telegramWebhook;

  @BeforeEach
  public void setup() {
    objectMapper = mock(ObjectMapper.class);
    userRequestMapper = mock(UserRequestMapper.class);
    commandDispatcher = mock(CommandDispatcher.class);
    telegramWebhook = new TelegramWebhook(objectMapper, userRequestMapper, commandDispatcher);
  }

  @Test
  public void shouldHandleUpdateSuccessfully() throws Exception {
    String testBody = "Some string";
    when(objectMapper.readValue(anyString(), eq(Update.class))).thenReturn(new Update());
    when(userRequestMapper.toUserRequest(any(Update.class)))
        .thenReturn(UserRequest.builder().chatId(1L).build());

    assertDoesNotThrow(() -> telegramWebhook.apply(Optional.of(testBody)));

    verify(objectMapper, times(1)).readValue(eq(testBody), eq(Update.class));
    verify(userRequestMapper, times(1)).toUserRequest(eq(new Update()));
    verify(commandDispatcher, times(1)).dispatch(eq(UserRequest.builder().chatId(1L).build()));
  }

  @Test
  public void shouldThrowExceptionIfRequestBodyIsNull() throws JsonProcessingException {
    assertThrows(InvalidRequestBodyException.class, () -> telegramWebhook.apply(Optional.empty()));

    verify(objectMapper, never()).readValue(anyString(), eq(Update.class));
    verify(userRequestMapper, never()).toUserRequest(any(Update.class));
    verify(commandDispatcher, never()).dispatch(any(UserRequest.class));
  }

  @Test
  public void shouldThrowExceptionIfParsingOfRequestBodyFailed() throws Exception {
    String testBody = "Wrong body";
    when(objectMapper.readValue(anyString(), eq(Update.class)))
        .thenThrow(JsonProcessingException.class);

    assertThrows(
        InvalidRequestBodyException.class, () -> telegramWebhook.apply(Optional.of(testBody)));

    verify(objectMapper, times(1)).readValue(eq(testBody), eq(Update.class));
    verify(userRequestMapper, never()).toUserRequest(any(Update.class));
    verify(commandDispatcher, never()).dispatch(any(UserRequest.class));
  }
}
