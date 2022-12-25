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

public class DeleteJobCommandHandlerTest {

    private JobService jobService;
    private TelegramService telegramService;
    private DeleteJobCommandHandler deleteJobCommandHandler;

    @BeforeEach
    public void setup() {
        jobService = mock(edu.kmaooad.service.JobService.class);
        telegramService = mock(TelegramService.class);
    }

    @Test
    public void shouldDeleteJob(){
        Long chatId = 1L;
        UserRequest userRequest = UserRequest.builder().chatId(chatId).text("/deleteJob").isCommand(true).build();
        UserState userState = new UserState(chatId, null, null, new HashMap<>());
        UserStateService userStateService = new UserStateServiceImpl();
        userStateService.setStateForUser(chatId, userState);
        deleteJobCommandHandler = new DeleteJobCommandHandler(telegramService, userStateService, jobService);
        Assertions.assertTrue(deleteJobCommandHandler.canHandle(Command.DELETE_JOB));
        Assertions.assertDoesNotThrow(() -> deleteJobCommandHandler.handle(userRequest));
    }
}
