package edu.kmaooad.command.handler.job;

import edu.kmaooad.command.dispatch.Command;
import edu.kmaooad.domain.model.UserState;
import edu.kmaooad.service.JobService;
import edu.kmaooad.service.TelegramService;
import edu.kmaooad.service.UserStateService;
import edu.kmaooad.service.UserStateServiceImpl;
import edu.kmaooad.web.request.UserRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.HashMap;

import static org.mockito.Mockito.mock;

public class AddJobCommandHandlerTest {

    private JobService jobService;
    private TelegramService telegramService;
    private AddJobCommandHandler addJobCommandHandler;

    @BeforeEach
    public void setup() {
        jobService = mock(JobService.class);
        telegramService = mock(TelegramService.class);
    }

    @Test
    public void shouldAddJob(){
        Long chatId = 1L;
        UserRequest userRequest = UserRequest.builder().chatId(chatId).text("/addJob").isCommand(true).build();
        UserState userState = new UserState(chatId, null, null, new HashMap<>());
        UserStateService userStateService = new UserStateServiceImpl();
        userStateService.setStateForUser(chatId, userState);
        addJobCommandHandler = new AddJobCommandHandler(telegramService, userStateService, jobService);
        Assertions.assertTrue(addJobCommandHandler.canHandle(Command.ADD_JOB));
        Assertions.assertDoesNotThrow(() -> addJobCommandHandler.handle(userRequest));
    }

    @Test
    public void shouldThrowExceptionWhenAddingJob(){
        UserRequest userRequest = UserRequest.builder().chatId(1L).text("/addJob").isCommand(true).build();
        addJobCommandHandler = new AddJobCommandHandler(telegramService, mock(UserStateService.class), jobService);
        Assertions.assertTrue(addJobCommandHandler.canHandle(Command.ADD_JOB));
        Assertions.assertThrows(Exception.class, () -> addJobCommandHandler.handle(userRequest));
    }

}
