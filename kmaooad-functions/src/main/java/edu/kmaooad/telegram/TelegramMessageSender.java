package edu.kmaooad.telegram;

import edu.kmaooad.config.TelegramProperties;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;

@Component
public class TelegramMessageSender extends DefaultAbsSender {

  private final TelegramProperties telegramProperties;

  public TelegramMessageSender(TelegramProperties telegramProperties) {
    super(new DefaultBotOptions());
    this.telegramProperties = telegramProperties;
  }

  @Override
  public String getBotToken() {
    return telegramProperties.getBotToken();
  }
}
