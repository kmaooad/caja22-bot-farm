package edu.kmaooad.telegram;

import edu.kmaooad.config.TelegramProperties;
import edu.kmaooad.service.TelegramService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.starter.SpringWebhookBot;

@Component
@Slf4j
public class TelegramBot extends SpringWebhookBot {

  private final TelegramProperties telegramProperties;
  private final TelegramService telegramService;

  public TelegramBot(TelegramProperties telegramProperties, TelegramService telegramService) {
    super(SetWebhook.builder().url(telegramProperties.getWebhookPath()).build());
    this.telegramProperties = telegramProperties;
    this.telegramService = telegramService;
  }

  @Override
  public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
    log.info("Received update: {}", update);
    final Message message = update.getMessage();
    telegramService.sendMessage(message.getChatId(), message.getText());
    return null;
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
