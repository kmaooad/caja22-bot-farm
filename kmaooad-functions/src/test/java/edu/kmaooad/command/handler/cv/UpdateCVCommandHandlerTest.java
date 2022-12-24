package edu.kmaooad.command.handler.cv;

import edu.kmaooad.command.dispatch.Command;
import edu.kmaooad.domain.model.UserState;
import edu.kmaooad.service.CVService;
import edu.kmaooad.service.TelegramService;
import edu.kmaooad.service.UserStateService;
import edu.kmaooad.service.UserStateServiceImpl;
import edu.kmaooad.web.request.UserRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.mockito.Mockito.mock;

public class UpdateCVCommandHandlerTest {

    private TelegramService telegramService;
    private CVService cvService;

    UpdateCVCommandHandler updateCVCommandHandler;

    @BeforeEach
    public void setup() {
        cvService = mock(CVService.class);
        telegramService = mock(TelegramService.class);
    }

    @Test
    public void shouldUpdateCV(){
        Long chatId = 4L;
        UserRequest userRequest = UserRequest.builder().chatId(chatId).text("/updateCV").isCommand(true).build();
        UserState userState = new UserState(chatId, null, null, new HashMap<>());
        UserStateService userStateService = new UserStateServiceImpl();
        userStateService.setStateForUser(chatId, userState);
        updateCVCommandHandler = new UpdateCVCommandHandler(telegramService, userStateService, cvService);
        Assertions.assertTrue(updateCVCommandHandler.canHandle(Command.UPDATE_CV));
        Assertions.assertDoesNotThrow(() -> updateCVCommandHandler.handle(userRequest));
    }

}