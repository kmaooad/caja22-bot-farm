package edu.kmaooad.command.handler.job;

import edu.kmaooad.command.dispatch.Command;
import edu.kmaooad.service.JobService;
import edu.kmaooad.service.TelegramService;
import edu.kmaooad.web.request.UserRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

public class GetJobsCommandHandlerTest {

    private GetJobsCommandHandler getJobsCommandHandler;

    @BeforeEach
    public void setup() {
        JobService jobService = mock(JobService.class);
        TelegramService telegramService = mock(TelegramService.class);
        getJobsCommandHandler = new GetJobsCommandHandler(jobService, telegramService);
    }

    @Test
    public void shouldGetJobs(){
        UserRequest userRequest = UserRequest.builder().chatId(1L).text("/getAllJobs").isCommand(true).build();
        Assertions.assertTrue(getJobsCommandHandler.canHandle(Command.GET_JOBS));
        Assertions.assertDoesNotThrow(() -> getJobsCommandHandler.handle(userRequest));
    }

}
