package edu.kmaooad.service;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

public interface TelegramService {

  void sendMessage(Long chatId, String message, ReplyKeyboard replyKeyboard);

  default void sendMessage(Long chatId, String message) {
    sendMessage(chatId, message, null);
  }
}
