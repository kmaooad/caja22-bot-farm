package edu.kmaooad.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.kmaooad.command.dispatch.CommandDispatcher;
import edu.kmaooad.domain.mapper.UserRequestMapper;
import edu.kmaooad.domain.model.UserRequest;
import edu.kmaooad.exception.InvalidRequestBodyException;
import java.util.Optional;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
@Slf4j
public class TelegramWebhook implements Function<Optional<String>, Void> {

  private static final Void EMPTY_OBJECT = null;
  private final ObjectMapper objectMapper;
  private final UserRequestMapper userRequestMapper;
  private final CommandDispatcher commandDispatcher;

  @Override
  public Void apply(Optional<String> requestBody) {
    requestBody
        .map(this::logRequestBody)
        .map(this::requestBodyToUpdate)
        .map(this::telegramUpdateToUserRequest)
        .ifPresentOrElse(commandDispatcher::dispatch, this::throwExceptionAboutNullBody);
    return EMPTY_OBJECT;
  }

  private String logRequestBody(String requestBody) {
    log.info("Request body: {}", requestBody);
    return requestBody;
  }

  private Update requestBodyToUpdate(String requestBody) {
    try {
      return objectMapper.readValue(requestBody, Update.class);
    } catch (JsonProcessingException ex) {
      log.error("Error while parsing request body!", ex);
      throw new InvalidRequestBodyException(ex.getMessage());
    }
  }

  private UserRequest telegramUpdateToUserRequest(Update update) {
    final UserRequest userRequest = userRequestMapper.toUserRequest(update);
    log.info("Mapped UserRequest: {}", userRequest);
    return userRequest;
  }

  private void throwExceptionAboutNullBody() {
    throw new InvalidRequestBodyException("Request body must not be null");
  }
}
