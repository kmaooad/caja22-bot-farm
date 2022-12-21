package edu.kmaooad.telegram;

import edu.kmaooad.config.TelegramProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TelegramMessageSenderTest {

  @Test
  public void shouldUseTelegramPropertiesForReadingToken() {
    String botToken = "bot-token";
    TelegramProperties telegramProperties = TelegramProperties.builder().botToken(botToken).build();
    TelegramMessageSender telegramMessageSender = new TelegramMessageSender(telegramProperties);

    Assertions.assertEquals(telegramMessageSender.getBotToken(), botToken);
  }
}
