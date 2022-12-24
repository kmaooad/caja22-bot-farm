package edu.kmaooad.command.handler.organization;

import edu.kmaooad.command.dispatch.Command;
import edu.kmaooad.domain.model.UserState;
import edu.kmaooad.service.*;
import edu.kmaooad.web.request.UserRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.mockito.Mockito.mock;

public class UpdateDepartmentStatusCommandHandlerTest {

    private TelegramService telegramService;
    private DepartmentService depService;
    private JobService jobService;

    private UpdateDepartmentStatusCommandHandler updateDepStatusCommandHandler;

    @BeforeEach
    public void setup() {
        jobService = mock(JobService.class);
        depService = mock(DepartmentService.class);
        telegramService = mock(TelegramService.class);
    }

    @Test
    public void shouldUpdateDepartmentStatus(){
        Long chatId = 1L;
        UserRequest userRequest = UserRequest.builder().chatId(chatId).text("/updateDepartmentStatus").isCommand(true).build();
        UserState userState = new UserState(chatId, null, null, new HashMap<>());
        UserStateService userStateService = new UserStateServiceImpl();
        userStateService.setStateForUser(chatId, userState);
        updateDepStatusCommandHandler = new UpdateDepartmentStatusCommandHandler(telegramService, userStateService, depService, jobService);
        Assertions.assertTrue(updateDepStatusCommandHandler.canHandle(Command.UPDATE_DEPARTMENT_STATUS));
        Assertions.assertDoesNotThrow(() -> updateDepStatusCommandHandler.handle(userRequest));
    }

}
