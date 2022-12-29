package edu.kmaooad.command.handler.common;

import static org.mockito.Mockito.mock;

import edu.kmaooad.command.dispatch.Command;
import edu.kmaooad.service.TelegramService;
import edu.kmaooad.web.request.UserRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UnknownCommandHandlerTest {

  private TelegramService telegramService;
  private UnknownCommandHandler unknownCommandHandler;

  @BeforeEach
  public void setup() {
    telegramService = mock(TelegramService.class);
  }

  @Test
  public void shouldSendUnknownCommand() {
    UserRequest userRequest =
        UserRequest.builder().chatId(2L).text("some-request").isCommand(false).build();
    unknownCommandHandler = new UnknownCommandHandler(telegramService);
    Assertions.assertTrue(unknownCommandHandler.canHandle(Command.UNKNOWN_COMMAND));
    Assertions.assertDoesNotThrow(() -> unknownCommandHandler.handle(userRequest));
  }
}
