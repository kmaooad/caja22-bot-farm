package edu.kmaooad.command.handler.common;

import edu.kmaooad.command.dispatch.Command;
import edu.kmaooad.domain.model.UserState;
import edu.kmaooad.service.TelegramService;
import edu.kmaooad.service.UserStateService;
import edu.kmaooad.service.UserStateServiceImpl;
import edu.kmaooad.web.request.UserRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.mockito.Mockito.mock;

public class CancelCommandHandlerTest {

    private TelegramService telegramService;
    CancelCommandHandler cancelCommandHandler;

    @BeforeEach
    public void setup() {
        telegramService = mock(TelegramService.class);
    }

    @Test
    public void shouldCancelCommand(){
        Long chatId = 1L;
        UserRequest userRequest = UserRequest.builder().chatId(chatId).text("/cancel").isCommand(true).build();
        UserState userState = new UserState(chatId, null, null, new HashMap<>());
        UserStateService userStateService = new UserStateServiceImpl();
        userStateService.setStateForUser(chatId, userState);
        cancelCommandHandler = new CancelCommandHandler(telegramService, userStateService);
        Assertions.assertTrue(cancelCommandHandler.canHandle(Command.CANCEL));
        Assertions.assertDoesNotThrow(() -> cancelCommandHandler.handle(userRequest));
    }

}