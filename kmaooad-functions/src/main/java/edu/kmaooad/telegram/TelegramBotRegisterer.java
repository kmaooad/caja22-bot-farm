package edu.kmaooad.telegram;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class TelegramBotRegisterer implements CommandLineRunner {

  private final WebClient webClient;

  @Override
  public void run(String... args) {
    webClient.post().retrieve().toBodilessEntity().block();
  }
}
