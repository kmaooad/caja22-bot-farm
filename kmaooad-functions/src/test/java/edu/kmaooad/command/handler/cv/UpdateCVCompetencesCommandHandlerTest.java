package edu.kmaooad.command.handler.cv;

import static org.mockito.Mockito.mock;

import edu.kmaooad.command.dispatch.Command;
import edu.kmaooad.domain.model.UserState;
import edu.kmaooad.service.CVService;
import edu.kmaooad.service.TelegramService;
import edu.kmaooad.service.UserStateService;
import edu.kmaooad.service.UserStateServiceImpl;
import edu.kmaooad.web.request.UserRequest;
import java.util.HashMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UpdateCVCompetencesCommandHandlerTest {

  private TelegramService telegramService;
  private CVService cvService;
  private UpdateCVCompetencesCommandHandler updateCVCompetencesCommandHandler;

  @BeforeEach
  public void setup() {
    cvService = mock(CVService.class);
    telegramService = mock(TelegramService.class);
  }

  @Test
  public void shouldUpdateCVCompetences() {
    Long chatId = 1L;
    UserRequest userRequest =
        UserRequest.builder().chatId(chatId).text("/updateCVCompetences").isCommand(true).build();
    UserState userState = new UserState(chatId, null, null, new HashMap<>());
    UserStateService userStateService = new UserStateServiceImpl();
    userStateService.setStateForUser(chatId, userState);
    updateCVCompetencesCommandHandler =
        new UpdateCVCompetencesCommandHandler(telegramService, userStateService, cvService);
    Assertions.assertTrue(
        updateCVCompetencesCommandHandler.canHandle(Command.UPDATE_CV_COMPETENCES));
    Assertions.assertDoesNotThrow(() -> updateCVCompetencesCommandHandler.handle(userRequest));
  }
}
