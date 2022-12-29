package edu.kmaooad.command.handler.common;

import static org.mockito.Mockito.mock;

import edu.kmaooad.command.dispatch.Command;
import edu.kmaooad.command.handler.job.AddJobCommandHandler;
import edu.kmaooad.domain.model.UserState;
import edu.kmaooad.service.TelegramService;
import edu.kmaooad.service.UserStateService;
import edu.kmaooad.service.UserStateServiceImpl;
import edu.kmaooad.web.request.UserRequest;
import java.util.HashMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CancelCommandHandlerTest {

  private TelegramService telegramService;
  private CancelCommandHandler cancelCommandHandler;

  @BeforeEach
  public void setup() {
    telegramService = mock(TelegramService.class);
  }

  @Test
  public void shouldClearNotEmptyUserStateAndCancelCommand() {
    Long chatId = 1L;
    UserRequest userRequest =
        UserRequest.builder().chatId(chatId).text("/cancel").isCommand(true).build();
    UserState userState =
        new UserState(
            chatId,
            Command.ADD_JOB,
            AddJobCommandHandler.AddJobState.WAITING_FOR_TITLE,
            new HashMap<>());
    UserStateService userStateService = new UserStateServiceImpl();
    userStateService.setStateForUser(chatId, userState);

    cancelCommandHandler = new CancelCommandHandler(telegramService, userStateService);

    Assertions.assertTrue(cancelCommandHandler.canHandle(Command.CANCEL));
    Assertions.assertDoesNotThrow(() -> cancelCommandHandler.handle(userRequest));
    Assertions.assertTrue(userStateService.getStateForUser(chatId).isEmpty());
  }
}
