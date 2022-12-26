package edu.kmaooad.command.handler.organization;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import edu.kmaooad.command.dispatch.Command;
import edu.kmaooad.domain.dto.department.UpdateDepartmentDTO;
import edu.kmaooad.domain.dto.job.UpdateJobDTO;
import edu.kmaooad.domain.model.Department;
import edu.kmaooad.domain.model.Job;
import edu.kmaooad.domain.model.UserState;
import edu.kmaooad.service.*;
import edu.kmaooad.web.request.UserRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UpdateDepartmentStatusCommandHandlerTest {

  private TelegramService telegramService;
  private DepartmentService depService;
  private JobService jobService;
  private UserStateService userStateService;

  private UpdateDepartmentStatusCommandHandler updateDepStatusCommandHandler;

  @BeforeEach
  public void setup() {
    jobService = mock(JobService.class);
    depService = mock(DepartmentService.class);
    telegramService = mock(TelegramService.class);
    userStateService = new UserStateServiceImpl();
    updateDepStatusCommandHandler =
        new UpdateDepartmentStatusCommandHandler(
            telegramService, userStateService, depService, jobService);
  }

  @Test
  public void shouldHandleUpdateDepartmentStatusCommand() {
    Assertions.assertTrue(
        updateDepStatusCommandHandler.canHandle(Command.UPDATE_DEPARTMENT_STATUS));
  }

  @Test
  public void shouldInitializeStateIfCurrentStateIsEmpty() {
    Long chatId = 1L;
    userStateService.setStateForUser(chatId, UserState.newEmptyState(chatId));
    UserRequest userRequest =
        UserRequest.builder()
            .chatId(chatId)
            .text("/updateDepartmentStatus")
            .isCommand(true)
            .build();

    updateDepStatusCommandHandler.handle(userRequest);

    UserState actual = userStateService.getStateForUser(chatId);
    Assertions.assertEquals(actual.getCurrentCommand(), Command.UPDATE_DEPARTMENT_STATUS);
    Assertions.assertEquals(
        actual.getCommandState(),
        UpdateDepartmentStatusCommandHandler.UpdateDepartmentStatusState
            .WAITING_FOR_UPDATE_STATUS_DEPARTMENT_ID);

    verify(telegramService, times(1))
        .sendMessage(
            eq(chatId),
            eq(
                UpdateDepartmentStatusCommandHandler.UpdateDepartmentStatusState
                    .WAITING_FOR_UPDATE_STATUS_DEPARTMENT_ID
                    .getMessage()));
  }

  @Test
  public void shouldWaitForDepartmentIdAndMoveToNextStateIfDepartmentExists() {
    when(depService.getDepartmentById(anyString()))
        .thenReturn(Optional.of(Department.builder().build()));
    String depId = "dep_id";
    Long chatId = 1L;
    userStateService.setStateForUser(
        chatId,
        new UserState(
            chatId,
            Command.UPDATE_DEPARTMENT_STATUS,
            UpdateDepartmentStatusCommandHandler.UpdateDepartmentStatusState
                .WAITING_FOR_UPDATE_STATUS_DEPARTMENT_ID,
            new HashMap<>()));
    UserRequest userRequest =
        UserRequest.builder().chatId(chatId).text(depId).isCommand(false).build();

    updateDepStatusCommandHandler.handle(userRequest);

    UserState actual = userStateService.getStateForUser(chatId);
    Assertions.assertEquals(actual.getCurrentCommand(), Command.UPDATE_DEPARTMENT_STATUS);
    Assertions.assertEquals(
        actual.getCommandState(),
        UpdateDepartmentStatusCommandHandler.UpdateDepartmentStatusState
            .WAITING_FOR_UPDATE_STATUS_DEPARTMENT_ACTION);
    Assertions.assertTrue(actual.getInputs().containsKey("id"));
    Assertions.assertEquals(actual.getInputs().get("id"), depId);

    verify(depService, times(2)).getDepartmentById(eq(depId));
    verify(telegramService, times(1)).sendMessage(eq(chatId), anyString());
  }

  @Test
  public void shouldWaitForDepartmentIdAndSendWarningMessageIfDepartmentDoesNotExist() {
    when(depService.getDepartmentById(anyString())).thenReturn(Optional.empty());
    String depId = "dep_id";
    Long chatId = 1L;
    userStateService.setStateForUser(
        chatId,
        new UserState(
            chatId,
            Command.UPDATE_DEPARTMENT_STATUS,
            UpdateDepartmentStatusCommandHandler.UpdateDepartmentStatusState
                .WAITING_FOR_UPDATE_STATUS_DEPARTMENT_ID,
            new HashMap<>()));
    UserRequest userRequest =
        UserRequest.builder().chatId(chatId).text(depId).isCommand(false).build();

    updateDepStatusCommandHandler.handle(userRequest);

    UserState actual = userStateService.getStateForUser(chatId);
    Assertions.assertEquals(actual.getCurrentCommand(), Command.UPDATE_DEPARTMENT_STATUS);
    Assertions.assertEquals(
        actual.getCommandState(),
        UpdateDepartmentStatusCommandHandler.UpdateDepartmentStatusState
            .WAITING_FOR_UPDATE_STATUS_DEPARTMENT_ID);

    verify(depService, times(1)).getDepartmentById(eq(depId));
    verify(telegramService, times(1)).sendMessage(eq(chatId), anyString());
  }

  @Test
  public void shouldEnableHiringIfInputIsPlus() {
    when(depService.getDepartmentById(anyString()))
        .thenReturn(Optional.of(Department.builder().build()));
    Map<String, String> inputs = new HashMap<>();
    String depId = "dep_id";
    String action = "+";
    inputs.put("id", depId);
    Long chatId = 1L;
    userStateService.setStateForUser(
        chatId,
        new UserState(
            chatId,
            Command.UPDATE_DEPARTMENT_STATUS,
            UpdateDepartmentStatusCommandHandler.UpdateDepartmentStatusState
                .WAITING_FOR_UPDATE_STATUS_DEPARTMENT_ACTION,
            inputs));
    UserRequest userRequest =
        UserRequest.builder().chatId(chatId).text(action).isCommand(false).build();

    updateDepStatusCommandHandler.handle(userRequest);

    UserState actual = userStateService.getStateForUser(chatId);
    Assertions.assertTrue(actual.isEmpty());

    verify(depService, times(1)).getDepartmentById(eq(depId));
    verify(depService, times(1))
        .updateDepStatus(eq(UpdateDepartmentDTO.builder().isHiring(true).build()));
    verify(telegramService, times(1)).sendMessage(eq(chatId), anyString());
  }

  @Test
  public void shouldDisableHiringIfInputIsMinus() {
    String depId = "dep_id";
    Map<String, String> inputs = new HashMap<>();
    String action = "-";
    inputs.put("id", depId);

    when(depService.getDepartmentById(anyString()))
        .thenReturn(Optional.of(Department.builder().id(depId).build()));
    when(jobService.getAllJobs()).thenReturn(List.of(Job.builder().depId(depId).build()));

    Long chatId = 1L;
    userStateService.setStateForUser(
        chatId,
        new UserState(
            chatId,
            Command.UPDATE_DEPARTMENT_STATUS,
            UpdateDepartmentStatusCommandHandler.UpdateDepartmentStatusState
                .WAITING_FOR_UPDATE_STATUS_DEPARTMENT_ACTION,
            inputs));
    UserRequest userRequest =
        UserRequest.builder().chatId(chatId).text(action).isCommand(false).build();

    updateDepStatusCommandHandler.handle(userRequest);

    UserState actual = userStateService.getStateForUser(chatId);
    Assertions.assertTrue(actual.isEmpty());

    verify(depService, times(1)).getDepartmentById(eq(depId));
    verify(depService, times(1))
        .updateDepStatus(eq(UpdateDepartmentDTO.builder().id(depId).isHiring(false).build()));
    verify(jobService, times(1)).getAllJobs();
    verify(jobService, times(1))
        .updateJob(eq(UpdateJobDTO.builder().isActive(false).depId(depId).build()));
    verify(telegramService, times(1)).sendMessage(eq(chatId), anyString());
  }

  @Test
  public void shouldSendWarningMessageIfInputIsInvalid() {
    when(depService.getDepartmentById(anyString()))
        .thenReturn(Optional.of(Department.builder().build()));
    Map<String, String> inputs = new HashMap<>();
    String depId = "dep_id";
    inputs.put("id", depId);
    Long chatId = 1L;
    userStateService.setStateForUser(
        chatId,
        new UserState(
            chatId,
            Command.UPDATE_DEPARTMENT_STATUS,
            UpdateDepartmentStatusCommandHandler.UpdateDepartmentStatusState
                .WAITING_FOR_UPDATE_STATUS_DEPARTMENT_ACTION,
            inputs));
    UserRequest userRequest =
        UserRequest.builder().chatId(chatId).text("some_text").isCommand(false).build();

    updateDepStatusCommandHandler.handle(userRequest);

    UserState actual = userStateService.getStateForUser(chatId);
    Assertions.assertEquals(actual.getCurrentCommand(), Command.UPDATE_DEPARTMENT_STATUS);
    Assertions.assertEquals(
        actual.getCommandState(),
        UpdateDepartmentStatusCommandHandler.UpdateDepartmentStatusState
            .WAITING_FOR_UPDATE_STATUS_DEPARTMENT_ACTION);

    verify(depService, times(1)).getDepartmentById(eq(depId));
    verify(telegramService, times(1)).sendMessage(eq(chatId), anyString());
  }
}
