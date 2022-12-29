package edu.kmaooad.command.handler.job;

import static org.mockito.Mockito.*;

import edu.kmaooad.command.dispatch.Command;
import edu.kmaooad.domain.dto.job.AddJobDTO;
import edu.kmaooad.domain.model.UserState;
import edu.kmaooad.service.JobService;
import edu.kmaooad.service.TelegramService;
import edu.kmaooad.service.UserStateService;
import edu.kmaooad.service.UserStateServiceImpl;
import edu.kmaooad.web.request.UserRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AddJobCommandHandlerTest {

  private JobService jobService;
  private TelegramService telegramService;
  private UserStateService userStateService;
  private AddJobCommandHandler addJobCommandHandler;

  @BeforeEach
  public void setup() {
    jobService = mock(JobService.class);
    telegramService = mock(TelegramService.class);
    userStateService = new UserStateServiceImpl();
    addJobCommandHandler = new AddJobCommandHandler(telegramService, userStateService, jobService);
  }

  @Test
  public void shouldHandleAddJobCommand() {
    Assertions.assertTrue(addJobCommandHandler.canHandle(Command.ADD_JOB));
  }

  @Test
  public void shouldInitializeStateIfCurrentStateIsEmpty() {
    Long chatId = 1L;
    userStateService.setStateForUser(chatId, UserState.newEmptyState(chatId));
    UserRequest userRequest =
        UserRequest.builder().chatId(chatId).text("/addJob").isCommand(true).build();

    addJobCommandHandler.handle(userRequest);

    UserState actual = userStateService.getStateForUser(chatId);
    Assertions.assertEquals(actual.getCurrentCommand(), Command.ADD_JOB);
    Assertions.assertEquals(
        actual.getCommandState(), AddJobCommandHandler.AddJobState.WAITING_FOR_TITLE);

    verify(telegramService, times(1))
        .sendMessage(
            eq(chatId), eq(AddJobCommandHandler.AddJobState.WAITING_FOR_TITLE.getMessage()));
  }

  @Test
  public void shouldWaitForTitleAndMoveToNextState() {
    Long chatId = 1L;
    userStateService.setStateForUser(
        chatId,
        new UserState(
            chatId,
            Command.ADD_JOB,
            AddJobCommandHandler.AddJobState.WAITING_FOR_TITLE,
            new HashMap<>()));
    UserRequest userRequest =
        UserRequest.builder().chatId(chatId).text("some_title").isCommand(false).build();

    addJobCommandHandler.handle(userRequest);

    UserState actual = userStateService.getStateForUser(chatId);
    Assertions.assertEquals(actual.getCurrentCommand(), Command.ADD_JOB);
    Assertions.assertEquals(
        actual.getCommandState(), AddJobCommandHandler.AddJobState.WAITING_FOR_DESCRIPTION);
    Assertions.assertTrue(actual.getInputs().containsKey("title"));
    Assertions.assertEquals(actual.getInputs().get("title"), "some_title");

    verify(telegramService, times(1))
        .sendMessage(
            eq(chatId), eq(AddJobCommandHandler.AddJobState.WAITING_FOR_DESCRIPTION.getMessage()));
  }

  @Test
  public void shouldWaitForDescriptionAndMoveToNextState() {
    Long chatId = 1L;
    userStateService.setStateForUser(
        chatId,
        new UserState(
            chatId,
            Command.ADD_JOB,
            AddJobCommandHandler.AddJobState.WAITING_FOR_DESCRIPTION,
            new HashMap<>()));
    UserRequest userRequest =
        UserRequest.builder().chatId(chatId).text("some_description").isCommand(false).build();

    addJobCommandHandler.handle(userRequest);

    UserState actual = userStateService.getStateForUser(chatId);
    Assertions.assertEquals(actual.getCurrentCommand(), Command.ADD_JOB);
    Assertions.assertEquals(
        actual.getCommandState(), AddJobCommandHandler.AddJobState.WAITING_FOR_ACTIVITIES);
    Assertions.assertTrue(actual.getInputs().containsKey("description"));
    Assertions.assertEquals(actual.getInputs().get("description"), "some_description");

    verify(telegramService, times(1))
        .sendMessage(
            eq(chatId), eq(AddJobCommandHandler.AddJobState.WAITING_FOR_ACTIVITIES.getMessage()));
  }

  @Test
  public void shouldWaitForActivitiesAndMoveToNextState() {
    Long chatId = 1L;
    userStateService.setStateForUser(
        chatId,
        new UserState(
            chatId,
            Command.ADD_JOB,
            AddJobCommandHandler.AddJobState.WAITING_FOR_ACTIVITIES,
            new HashMap<>()));
    UserRequest userRequest =
        UserRequest.builder().chatId(chatId).text("some_activities").isCommand(false).build();

    addJobCommandHandler.handle(userRequest);

    UserState actual = userStateService.getStateForUser(chatId);
    Assertions.assertEquals(actual.getCurrentCommand(), Command.ADD_JOB);
    Assertions.assertEquals(
        actual.getCommandState(), AddJobCommandHandler.AddJobState.WAITING_FOR_COMPETENCES);
    Assertions.assertTrue(actual.getInputs().containsKey("activities"));
    Assertions.assertEquals(actual.getInputs().get("activities"), "some_activities");

    verify(telegramService, times(1))
        .sendMessage(
            eq(chatId), eq(AddJobCommandHandler.AddJobState.WAITING_FOR_COMPETENCES.getMessage()));
  }

  @Test
  public void shouldWaitForCompetencesAndMoveToNextState() {
    Long chatId = 1L;
    userStateService.setStateForUser(
        chatId,
        new UserState(
            chatId,
            Command.ADD_JOB,
            AddJobCommandHandler.AddJobState.WAITING_FOR_COMPETENCES,
            new HashMap<>()));
    UserRequest userRequest =
        UserRequest.builder().chatId(chatId).text("some_competences").isCommand(false).build();

    addJobCommandHandler.handle(userRequest);

    UserState actual = userStateService.getStateForUser(chatId);
    Assertions.assertEquals(actual.getCurrentCommand(), Command.ADD_JOB);
    Assertions.assertEquals(
        actual.getCommandState(), AddJobCommandHandler.AddJobState.WAITING_FOR_DEPARTMENT);
    Assertions.assertTrue(actual.getInputs().containsKey("competences"));
    Assertions.assertEquals(actual.getInputs().get("competences"), "some_competences");

    verify(telegramService, times(1))
        .sendMessage(
            eq(chatId), eq(AddJobCommandHandler.AddJobState.WAITING_FOR_DEPARTMENT.getMessage()));
  }

  @Test
  public void shouldWaitForDepartmentAndAddNewJob() {
    Map<String, String> inputs = new HashMap<>();
    String title = "some_title";
    String description = "some_description";
    String activities = "some_activities";
    String competences = "some_competences";
    String departmentId = "some_department";
    inputs.put("title", title);
    inputs.put("description", description);
    inputs.put("activities", activities);
    inputs.put("competences", competences);
    Long chatId = 1L;
    userStateService.setStateForUser(
        chatId,
        new UserState(
            chatId,
            Command.ADD_JOB,
            AddJobCommandHandler.AddJobState.WAITING_FOR_DEPARTMENT,
            inputs));
    UserRequest userRequest =
        UserRequest.builder().chatId(chatId).text(departmentId).isCommand(false).build();

    addJobCommandHandler.handle(userRequest);

    UserState actual = userStateService.getStateForUser(chatId);
    Assertions.assertTrue(actual.isEmpty());

    verify(jobService, times(1))
        .addJob(
            eq(
                AddJobDTO.builder()
                    .title(title)
                    .description(description)
                    .activities(List.of(activities))
                    .competences(List.of(competences))
                    .depId(departmentId)
                    .build()));
    verify(telegramService, times(1)).sendMessage(eq(chatId), anyString());
  }
}
