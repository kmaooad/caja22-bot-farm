package edu.kmaooad.utils;

import edu.kmaooad.domain.AddMessage;
import edu.kmaooad.domain.AddMessageResult;
import edu.kmaooad.parser.RequestParser;
import edu.kmaooad.web.TelegramWebhook;
import java.util.function.Function;

public class TelegramWebhookProxy implements Function<AddMessage, AddMessageResult> {

  private final TelegramWebhook telegramWebhook;

  public TelegramWebhookProxy() {
    this.telegramWebhook =
        new TelegramWebhook(new RequestParser(), new TestMessageRepositoryImpl());
  }

  @Override
  public AddMessageResult apply(AddMessage addMessage) {
    return telegramWebhook.apply(addMessage);
  }
}
