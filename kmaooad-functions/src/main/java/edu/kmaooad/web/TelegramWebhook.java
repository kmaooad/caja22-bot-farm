package edu.kmaooad.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.kmaooad.domain.AddMessage;
import edu.kmaooad.domain.AddMessageResult;
import edu.kmaooad.domain.Message;
import edu.kmaooad.repository.MessageRepository;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TelegramWebhook implements Function<AddMessage, AddMessageResult> {

  private final ObjectMapper objectMapper;
  private final MessageRepository messageRepository;

  @Override
  public AddMessageResult apply(AddMessage addMessage) {
    final String messageStr = addMessage.getMessage();

    try {
      final Message message = objectMapper.readValue(messageStr, Message.class);

      messageRepository.insert(message);

      return new AddMessageResult(true, String.valueOf(message.getMessage().getMessageId()), null);
    } catch (JsonProcessingException ex) {
      return new AddMessageResult(false, null, "Can't get message_id from request");
    }
  }
}
