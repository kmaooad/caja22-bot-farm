package edu.kmaooad.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.kmaooad.domain.AddMessageResult;
import edu.kmaooad.telegram.TelegramBot;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class TelegramWebhook implements Function<Update, AddMessageResult> {

  private final ObjectMapper objectMapper;
  private final TelegramBot telegramBot;

  @Override
  public AddMessageResult apply(Update update) {
    telegramBot.onWebhookUpdateReceived(update);
    return new AddMessageResult(true, "Success", null);
  }
}
