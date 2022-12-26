package edu.kmaooad.command.handler.cv;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import edu.kmaooad.command.dispatch.Command;
import edu.kmaooad.domain.dto.cv.UpdateCVDTO;
import edu.kmaooad.domain.model.CV;
import edu.kmaooad.domain.model.UserState;
import edu.kmaooad.service.CVService;
import edu.kmaooad.service.TelegramService;
import edu.kmaooad.service.UserStateService;
import edu.kmaooad.service.UserStateServiceImpl;
import edu.kmaooad.web.request.UserRequest;
import java.util.HashMap;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ToggleActiveCVCommandHandlerTest {

  private TelegramService telegramService;
  private CVService cvService;
  private UserStateService userStateService;
  private ToggleActiveCVCommandHandler toggleActiveCVCommandHandler;

  @BeforeEach
  public void setup() {
    cvService = mock(CVService.class);
    telegramService = mock(TelegramService.class);
    userStateService = new UserStateServiceImpl();
    toggleActiveCVCommandHandler =
        new ToggleActiveCVCommandHandler(telegramService, userStateService, cvService);
  }

  @Test
  public void shouldHandleToggleCVHiresCommand() {
    Assertions.assertTrue(toggleActiveCVCommandHandler.canHandle(Command.TOGGLE_CV_HIRES));
  }

  @Test
  public void shouldInitializeStateIfCurrentStateIsEmpty() {
    Long chatId = 1L;
    userStateService.setStateForUser(chatId, UserState.newEmptyState(chatId));
    UserRequest userRequest =
        UserRequest.builder().chatId(chatId).text("/toggleCVHires").isCommand(true).build();

    toggleActiveCVCommandHandler.handle(userRequest);

    UserState actual = userStateService.getStateForUser(chatId);
    Assertions.assertEquals(actual.getCurrentCommand(), Command.TOGGLE_CV_HIRES);
    Assertions.assertEquals(
        actual.getCommandState(),
        ToggleActiveCVCommandHandler.ToggleHireCVState.WAITING_FOR_TOGGLE_CV_DECISION);

    verify(telegramService, times(1))
        .sendMessage(
            eq(chatId),
            eq(
                ToggleActiveCVCommandHandler.ToggleHireCVState.WAITING_FOR_TOGGLE_CV_DECISION
                    .getMessage()));
  }

  @Test
  public void shouldWaitForToggleCVDecisionAndMoveToTogglingByNameIfNameOptionSpecified() {
    Long chatId = 1L;
    userStateService.setStateForUser(
        chatId,
        new UserState(
            chatId,
            Command.TOGGLE_CV_OPEN,
            ToggleActiveCVCommandHandler.ToggleHireCVState.WAITING_FOR_TOGGLE_CV_DECISION,
            new HashMap<>()));
    UserRequest userRequest =
        UserRequest.builder().chatId(chatId).text("Y").isCommand(false).build();

    toggleActiveCVCommandHandler.handle(userRequest);

    UserState actual = userStateService.getStateForUser(chatId);
    Assertions.assertEquals(actual.getCurrentCommand(), Command.TOGGLE_CV_OPEN);
    Assertions.assertEquals(
        actual.getCommandState(),
        ToggleActiveCVCommandHandler.ToggleHireCVState.WAITING_FOR_TOGGLE_CV_NAME);

    verify(telegramService, times(1)).sendMessage(eq(chatId), anyString());
  }

  @Test
  public void shouldWaitForToggleCVDecisionAndMoveToTogglingByIdIfIdOptionSpecified() {
    Long chatId = 1L;
    userStateService.setStateForUser(
        chatId,
        new UserState(
            chatId,
            Command.TOGGLE_CV_OPEN,
            ToggleActiveCVCommandHandler.ToggleHireCVState.WAITING_FOR_TOGGLE_CV_DECISION,
            new HashMap<>()));
    UserRequest userRequest =
        UserRequest.builder().chatId(chatId).text("N").isCommand(false).build();

    toggleActiveCVCommandHandler.handle(userRequest);

    UserState actual = userStateService.getStateForUser(chatId);
    Assertions.assertEquals(actual.getCurrentCommand(), Command.TOGGLE_CV_OPEN);
    Assertions.assertEquals(
        actual.getCommandState(),
        ToggleActiveCVCommandHandler.ToggleHireCVState.WAITING_FOR_TOGGLE_CV_ID);

    verify(telegramService, times(1)).sendMessage(eq(chatId), anyString());
  }

  @Test
  public void shouldWaitForCVNameAndToggleCVHiresIfCVExists() {
    String cvName = "some_name";
    when(cvService.getCVByName(anyString()))
        .thenReturn(Optional.of(CV.builder().isActive(false).build()));
    Long chatId = 1L;
    userStateService.setStateForUser(
        chatId,
        new UserState(
            chatId,
            Command.TOGGLE_CV_OPEN,
            ToggleActiveCVCommandHandler.ToggleHireCVState.WAITING_FOR_TOGGLE_CV_NAME,
            new HashMap<>()));
    UserRequest userRequest =
        UserRequest.builder().chatId(chatId).text(cvName).isCommand(false).build();

    toggleActiveCVCommandHandler.handle(userRequest);

    UserState actual = userStateService.getStateForUser(chatId);
    Assertions.assertTrue(actual.isEmpty());

    verify(cvService, times(1)).getCVByName(eq(cvName));
    verify(cvService, times(1)).updateCV(eq(UpdateCVDTO.builder().isActive(true).build()));
    verify(telegramService, times(1)).sendMessage(eq(chatId), anyString());
  }

  @Test
  public void shouldWaitForCVNameAndSendWarningMessageIfCVDoesNotExist() {
    String cvName = "some_name";
    when(cvService.getCVByName(anyString())).thenReturn(Optional.empty());
    Long chatId = 1L;
    userStateService.setStateForUser(
        chatId,
        new UserState(
            chatId,
            Command.TOGGLE_CV_OPEN,
            ToggleActiveCVCommandHandler.ToggleHireCVState.WAITING_FOR_TOGGLE_CV_NAME,
            new HashMap<>()));
    UserRequest userRequest =
        UserRequest.builder().chatId(chatId).text(cvName).isCommand(false).build();

    toggleActiveCVCommandHandler.handle(userRequest);

    verify(cvService, times(1)).getCVByName(eq(cvName));
    verify(cvService, never()).updateCV(any(UpdateCVDTO.class));
    verify(telegramService, times(1)).sendMessage(eq(chatId), anyString());
  }

  @Test
  public void shouldWaitForCVIdAndToggleCVHiresIfCVExists() {
    String cvId = "cv_id";
    when(cvService.getCVById(anyString()))
        .thenReturn(Optional.of(CV.builder().isActive(false).build()));
    Long chatId = 1L;
    userStateService.setStateForUser(
        chatId,
        new UserState(
            chatId,
            Command.TOGGLE_CV_OPEN,
            ToggleActiveCVCommandHandler.ToggleHireCVState.WAITING_FOR_TOGGLE_CV_ID,
            new HashMap<>()));
    UserRequest userRequest =
        UserRequest.builder().chatId(chatId).text(cvId).isCommand(false).build();

    toggleActiveCVCommandHandler.handle(userRequest);

    UserState actual = userStateService.getStateForUser(chatId);
    Assertions.assertTrue(actual.isEmpty());

    verify(cvService, times(2)).getCVById(eq(cvId));
    verify(cvService, times(1)).updateCV(eq(UpdateCVDTO.builder().isActive(true).build()));
    verify(telegramService, times(1)).sendMessage(eq(chatId), anyString());
  }

  @Test
  public void shouldWaitForCVIdAndSendWarningMessageIfCVDoesNotExist() {
    String cvId = "cv_id";
    when(cvService.getCVById(anyString())).thenReturn(Optional.empty());
    Long chatId = 1L;
    userStateService.setStateForUser(
        chatId,
        new UserState(
            chatId,
            Command.TOGGLE_CV_OPEN,
            ToggleActiveCVCommandHandler.ToggleHireCVState.WAITING_FOR_TOGGLE_CV_ID,
            new HashMap<>()));
    UserRequest userRequest =
        UserRequest.builder().chatId(chatId).text(cvId).isCommand(false).build();

    toggleActiveCVCommandHandler.handle(userRequest);

    verify(cvService, times(1)).getCVById(eq(cvId));
    verify(cvService, never()).updateCV(any(UpdateCVDTO.class));
    verify(telegramService, times(1)).sendMessage(eq(chatId), anyString());
  }
}
