package edu.kmaooad.web;

import edu.kmaooad.domain.AddMessage;
import edu.kmaooad.domain.AddMessageResult;
import edu.kmaooad.exception.InvalidMessageException;
import edu.kmaooad.parser.RequestParser;
import edu.kmaooad.repository.MessageRepository;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TelegramWebhook implements Function<AddMessage, AddMessageResult> {

  private final RequestParser requestParser;
  private final MessageRepository messageRepository;

  @Override
  public AddMessageResult apply(AddMessage addMessage) {
    final String message = addMessage.getMessage();

    messageRepository.insertMessage(message);

    try {
      final Integer messageId = requestParser.getMessageId(message);
      return new AddMessageResult(true, String.valueOf(messageId), null);
    } catch (InvalidMessageException ex) {
      return new AddMessageResult(false, null, ex.getMessage());
    }
  }
}
