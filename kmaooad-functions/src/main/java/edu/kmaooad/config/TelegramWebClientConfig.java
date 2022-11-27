package edu.kmaooad.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class TelegramWebClientConfig {

  private final TelegramProperties telegramProperties;

  @Bean
  public WebClient webClient() {
    final String setWebhookUrl =
        String.format(
            telegramProperties.getSetWebhookPath(),
            telegramProperties.getBotToken(),
            telegramProperties.getWebhookPath());
    return WebClient.builder().baseUrl(setWebhookUrl).build();
  }
}
