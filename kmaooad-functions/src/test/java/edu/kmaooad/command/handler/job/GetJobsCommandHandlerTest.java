package edu.kmaooad.command.handler.job;

import static org.mockito.Mockito.*;

import edu.kmaooad.command.dispatch.Command;
import edu.kmaooad.domain.model.Job;
import edu.kmaooad.service.JobService;
import edu.kmaooad.service.TelegramService;
import edu.kmaooad.web.request.UserRequest;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GetJobsCommandHandlerTest {

  private JobService jobService;
  private TelegramService telegramService;
  private GetJobsCommandHandler getJobsCommandHandler;

  @BeforeEach
  public void setup() {
    jobService = mock(JobService.class);
    telegramService = mock(TelegramService.class);
    getJobsCommandHandler = new GetJobsCommandHandler(jobService, telegramService);
  }

  @Test
  public void shouldHandleGetJobsCommand() {
    Assertions.assertTrue(getJobsCommandHandler.canHandle(Command.GET_JOBS));
  }

  @Test
  public void shouldGetJobs() {
    Long chatId = 1L;
    when(jobService.getAllJobs()).thenReturn(List.of(Job.builder().build()));

    UserRequest userRequest =
        UserRequest.builder().chatId(chatId).text("/getAllJobs").isCommand(true).build();

    getJobsCommandHandler.handle(userRequest);

    verify(jobService, times(1)).getAllJobs();
    verify(telegramService, times(1)).sendMessage(eq(chatId), anyString());
  }
}
