package edu.kmaooad.telegram;

import edu.kmaooad.config.TelegramProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.starter.SpringWebhookBot;

@Component
@Slf4j
public class TelegramBot extends SpringWebhookBot {

  private final TelegramProperties telegramProperties;

  public TelegramBot(TelegramProperties telegramProperties) {
    super(SetWebhook.builder().url(telegramProperties.getWebhookPath()).build());
    this.telegramProperties = telegramProperties;
  }

  @Override
  public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
    log.info("Received update: {}", update);
    return SendMessage.builder()
        .chatId(update.getMessage().getChatId())
        .text(update.getMessage().getText())
        .build();
  }

  @Override
  public String getBotUsername() {
    return telegramProperties.getBotName();
  }

  @Override
  public String getBotToken() {
    return telegramProperties.getBotToken();
  }

  @Override
  public String getBotPath() {
    return telegramProperties.getWebhookPath();
  }
}
