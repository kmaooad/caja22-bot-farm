package edu.kmaooad.command.handler.job;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import edu.kmaooad.command.dispatch.Command;
import edu.kmaooad.domain.model.Job;
import edu.kmaooad.domain.model.UserState;
import edu.kmaooad.service.JobService;
import edu.kmaooad.service.TelegramService;
import edu.kmaooad.service.UserStateService;
import edu.kmaooad.service.UserStateServiceImpl;
import edu.kmaooad.web.request.UserRequest;
import java.util.HashMap;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DeleteJobCommandHandlerTest {

  private JobService jobService;
  private TelegramService telegramService;
  private UserStateService userStateService;
  private DeleteJobCommandHandler deleteJobCommandHandler;

  @BeforeEach
  public void setup() {
    jobService = mock(JobService.class);
    telegramService = mock(TelegramService.class);
    userStateService = new UserStateServiceImpl();
    deleteJobCommandHandler =
        new DeleteJobCommandHandler(telegramService, userStateService, jobService);
  }

  @Test
  public void shouldHandleDeleteJobCommand() {
    Assertions.assertTrue(deleteJobCommandHandler.canHandle(Command.DELETE_JOB));
  }

  @Test
  public void shouldInitializeStateIfCurrentStateIsEmpty() {
    Long chatId = 1L;
    userStateService.setStateForUser(chatId, UserState.newEmptyState(chatId));
    UserRequest userRequest =
        UserRequest.builder().chatId(chatId).text("/deleteJob").isCommand(true).build();

    deleteJobCommandHandler.handle(userRequest);

    UserState actual = userStateService.getStateForUser(chatId);
    Assertions.assertEquals(actual.getCurrentCommand(), Command.DELETE_JOB);
    Assertions.assertEquals(
        actual.getCommandState(), DeleteJobCommandHandler.DeleteJobState.WAITING_FOR_JOB_ID);

    verify(telegramService, times(1))
        .sendMessage(
            eq(chatId), eq(DeleteJobCommandHandler.DeleteJobState.WAITING_FOR_JOB_ID.getMessage()));
  }

  @Test
  public void shouldWaitForJobIdAndDeleteJobIfJobExists() {
    String jobId = "job_id";
    when(jobService.getJobById(anyString()))
        .thenReturn(Optional.of(Job.builder().id(jobId).build()));
    Long chatId = 1L;
    userStateService.setStateForUser(
        chatId,
        new UserState(
            chatId,
            Command.DELETE_JOB,
            DeleteJobCommandHandler.DeleteJobState.WAITING_FOR_JOB_ID,
            new HashMap<>()));
    UserRequest userRequest =
        UserRequest.builder().chatId(chatId).text(jobId).isCommand(false).build();

    deleteJobCommandHandler.handle(userRequest);

    UserState actual = userStateService.getStateForUser(chatId);
    Assertions.assertTrue(actual.isEmpty());

    verify(jobService, times(1)).deleteJob(eq(jobId));
    verify(telegramService, times(1)).sendMessage(eq(chatId), anyString());
  }

  @Test
  public void shouldWaitForJobIdAndSendWarningMessageIfJobDoesNotExist() {
    String jobId = "job_id";
    when(jobService.getJobById(anyString())).thenReturn(Optional.empty());
    Long chatId = 1L;
    userStateService.setStateForUser(
        chatId,
        new UserState(
            chatId,
            Command.DELETE_JOB,
            DeleteJobCommandHandler.DeleteJobState.WAITING_FOR_JOB_ID,
            new HashMap<>()));
    UserRequest userRequest =
        UserRequest.builder().chatId(chatId).text(jobId).isCommand(false).build();

    deleteJobCommandHandler.handle(userRequest);

    UserState actual = userStateService.getStateForUser(chatId);
    Assertions.assertFalse(actual.isEmpty());

    verify(jobService, never()).deleteJob(anyString());
    verify(telegramService, times(1)).sendMessage(eq(chatId), anyString());
  }
}
