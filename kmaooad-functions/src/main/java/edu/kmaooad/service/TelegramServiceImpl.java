package edu.kmaooad.service;

import edu.kmaooad.sender.TelegramMessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@RequiredArgsConstructor
@Slf4j
public class TelegramServiceImpl implements TelegramService {

  private final TelegramMessageSender telegramMessageSender;

  @Override
  public void sendMessage(Long chatId, String message, ReplyKeyboard replyKeyboard) {
    final SendMessage sendMessage =
        SendMessage.builder()
            .chatId(chatId)
            .text(message)
            .parseMode(ParseMode.HTML)
            .replyMarkup(replyKeyboard)
            .build();
    try {
      telegramMessageSender.execute(sendMessage);
    } catch (TelegramApiException e) {
      log.error("Error while sending message to telegram!", e);
    }
  }
}
