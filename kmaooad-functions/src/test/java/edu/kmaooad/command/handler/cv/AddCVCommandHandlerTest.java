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

public class AddCVCommandHandlerTest {

    private TelegramService telegramService;
    private CVService cvService;

    AddCVCommandHandler addCVCommandHandler;

    @BeforeEach
    public void setup() {
        cvService = mock(CVService.class);
        telegramService = mock(TelegramService.class);
    }

    @Test
    public void shouldAddCV(){
        Long chatId = 1L;
        UserRequest userRequest = UserRequest.builder().chatId(chatId).text("/addCV").isCommand(true).build();
        UserState userState = new UserState(chatId, null, null, new HashMap<>());
        UserStateService userStateService = new UserStateServiceImpl();
        userStateService.setStateForUser(chatId, userState);
        addCVCommandHandler = new AddCVCommandHandler(telegramService, userStateService, cvService);
        Assertions.assertTrue(addCVCommandHandler.canHandle(Command.ADD_CV));
        Assertions.assertDoesNotThrow(() -> addCVCommandHandler.handle(userRequest));
    }

}
