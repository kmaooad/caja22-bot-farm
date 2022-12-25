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

public class ToggleOpenCVCommandHandlerTest {

    private TelegramService telegramService;
    private CVService cvService;
    private ToggleOpenCVCommandHandler toggleOpenCVCommandHandler;

    @BeforeEach
    public void setup() {
        cvService = mock(CVService.class);
        telegramService = mock(TelegramService.class);
    }

    @Test
    public void shouldToggleOpenCV(){
        Long chatId = 1L;
        UserRequest userRequest = UserRequest.builder().chatId(chatId).text("/toggleCVOpen").isCommand(true).build();
        UserState userState = new UserState(chatId, null, null, new HashMap<>());
        UserStateService userStateService = new UserStateServiceImpl();
        userStateService.setStateForUser(chatId, userState);
        toggleOpenCVCommandHandler = new ToggleOpenCVCommandHandler(telegramService, userStateService, cvService);
        Assertions.assertTrue(toggleOpenCVCommandHandler.canHandle(Command.TOGGLE_CV_OPEN));
        Assertions.assertDoesNotThrow(() -> toggleOpenCVCommandHandler.handle(userRequest));
    }

}