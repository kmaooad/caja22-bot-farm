package edu.kmaooad.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.kmaooad.command.dispatch.CommandDispatcher;
import edu.kmaooad.domain.mapper.UserRequestMapper;
import edu.kmaooad.domain.model.UserRequest;
import java.util.Objects;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
@Slf4j
public class TelegramWebhook implements Function<String, Void> {

  private static final Void EMPTY_OBJECT = null;
  private final ObjectMapper objectMapper;
  private final UserRequestMapper userRequestMapper;
  private final CommandDispatcher commandDispatcher;

  @Override
  public Void apply(String requestBody) {
    Objects.requireNonNull(requestBody);
    log.info("Request body: {}", requestBody);

    try {
      final Update update = objectMapper.readValue(requestBody, Update.class);
      final UserRequest userRequest = userRequestMapper.toUserRequest(update);
      log.info("Mapped UserRequest: {}", userRequest);
      commandDispatcher.dispatch(userRequest);
      return EMPTY_OBJECT;
    } catch (JsonProcessingException ex) {
      throw new RuntimeException(ex);
    }
  }
}
