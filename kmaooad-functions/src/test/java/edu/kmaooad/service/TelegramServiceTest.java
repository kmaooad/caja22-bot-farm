package edu.kmaooad.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import edu.kmaooad.telegram.TelegramMessageSender;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class TelegramServiceTest {

  private TelegramMessageSender telegramMessageSender;
  private TelegramService telegramService;

  @BeforeEach
  public void setup() {
    telegramMessageSender = mock(TelegramMessageSender.class);
    telegramService = new TelegramServiceImpl(telegramMessageSender);
  }

  @Test
  public void shouldSendMessageWithoutKeyboardSuccessfully() throws TelegramApiException {
    Long chatId = 1L;
    String message = "message";
    when(telegramMessageSender.execute(any(SendMessage.class))).thenReturn(null);

    Assertions.assertDoesNotThrow(() -> telegramService.sendMessage(chatId, message));

    verify(telegramMessageSender, times(1))
        .execute(
            eq(
                SendMessage.builder()
                    .chatId(chatId)
                    .text(message)
                    .parseMode(ParseMode.HTML)
                    .replyMarkup(null)
                    .build()));
  }

  @Test
  public void shouldSendMessageWithKeyboardSuccessfully() throws TelegramApiException {
    Long chatId = 1L;
    String message = "message";
    ReplyKeyboard replyKeyboard = new ReplyKeyboardMarkup();
    when(telegramMessageSender.execute(any(SendMessage.class))).thenReturn(null);

    Assertions.assertDoesNotThrow(
        () -> telegramService.sendMessage(chatId, message, replyKeyboard));

    verify(telegramMessageSender, times(1))
        .execute(
            eq(
                SendMessage.builder()
                    .chatId(chatId)
                    .text(message)
                    .parseMode(ParseMode.HTML)
                    .replyMarkup(replyKeyboard)
                    .build()));
  }

  @Test
  public void shouldThrowExceptionWhenSendingMessageFailed() throws TelegramApiException {
    Long chatId = 1L;
    String message = "message";
    when(telegramMessageSender.execute(any(SendMessage.class)))
        .thenThrow(TelegramApiException.class);

    Assertions.assertThrows(
        RuntimeException.class, () -> telegramService.sendMessage(chatId, message));

    verify(telegramMessageSender, times(1))
        .execute(
            eq(
                SendMessage.builder()
                    .chatId(chatId)
                    .text(message)
                    .parseMode(ParseMode.HTML)
                    .replyMarkup(null)
                    .build()));
  }
}
