package edu.kmaooad.command.handler.cv;

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

public class ToggleOpenCVCommandHandlerTest {

  private TelegramService telegramService;
  private CVService cvService;
  private UserStateService userStateService;
  private ToggleOpenCVCommandHandler toggleOpenCVCommandHandler;

  @BeforeEach
  public void setup() {
    cvService = mock(CVService.class);
    telegramService = mock(TelegramService.class);
    userStateService = new UserStateServiceImpl();
    toggleOpenCVCommandHandler =
        new ToggleOpenCVCommandHandler(telegramService, userStateService, cvService);
  }

  @Test
  public void shouldHandleToggleOpenCVCommand() {
    Assertions.assertTrue(toggleOpenCVCommandHandler.canHandle(Command.TOGGLE_CV_OPEN));
  }

  @Test
  public void shouldInitializeStateIfCurrentStateIsEmpty() {
    Long chatId = 1L;
    userStateService.setStateForUser(chatId, UserState.newEmptyState(chatId));
    UserRequest userRequest =
        UserRequest.builder().chatId(chatId).text("/toggleCVOpen").isCommand(true).build();

    toggleOpenCVCommandHandler.handle(userRequest);

    UserState actual = userStateService.getStateForUser(chatId);
    Assertions.assertEquals(actual.getCurrentCommand(), Command.TOGGLE_CV_OPEN);
    Assertions.assertEquals(
        actual.getCommandState(),
        ToggleOpenCVCommandHandler.ToggleOpenCVState.WAITING_FOR_TOGGLE_CV_DECISION);

    verify(telegramService, times(1))
        .sendMessage(
            eq(chatId),
            eq(
                ToggleOpenCVCommandHandler.ToggleOpenCVState.WAITING_FOR_TOGGLE_CV_DECISION
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
            ToggleOpenCVCommandHandler.ToggleOpenCVState.WAITING_FOR_TOGGLE_CV_DECISION,
            new HashMap<>()));
    UserRequest userRequest =
        UserRequest.builder().chatId(chatId).text("Y").isCommand(false).build();

    toggleOpenCVCommandHandler.handle(userRequest);

    UserState actual = userStateService.getStateForUser(chatId);
    Assertions.assertEquals(actual.getCurrentCommand(), Command.TOGGLE_CV_OPEN);
    Assertions.assertEquals(
        actual.getCommandState(),
        ToggleOpenCVCommandHandler.ToggleOpenCVState.WAITING_FOR_TOGGLE_CV_NAME);

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
            ToggleOpenCVCommandHandler.ToggleOpenCVState.WAITING_FOR_TOGGLE_CV_DECISION,
            new HashMap<>()));
    UserRequest userRequest =
        UserRequest.builder().chatId(chatId).text("N").isCommand(false).build();

    toggleOpenCVCommandHandler.handle(userRequest);

    UserState actual = userStateService.getStateForUser(chatId);
    Assertions.assertEquals(actual.getCurrentCommand(), Command.TOGGLE_CV_OPEN);
    Assertions.assertEquals(
        actual.getCommandState(),
        ToggleOpenCVCommandHandler.ToggleOpenCVState.WAITING_FOR_TOGGLE_CV_ID);

    verify(telegramService, times(1)).sendMessage(eq(chatId), anyString());
  }

  @Test
  public void shouldWaitForCVNameAndToggleCVOpenIfCVExists() {
    String cvName = "some_name";
    when(cvService.getCVByName(anyString()))
        .thenReturn(Optional.of(CV.builder().isHidden(true).build()));
    Long chatId = 1L;
    userStateService.setStateForUser(
        chatId,
        new UserState(
            chatId,
            Command.TOGGLE_CV_OPEN,
            ToggleOpenCVCommandHandler.ToggleOpenCVState.WAITING_FOR_TOGGLE_CV_NAME,
            new HashMap<>()));
    UserRequest userRequest =
        UserRequest.builder().chatId(chatId).text(cvName).isCommand(false).build();

    toggleOpenCVCommandHandler.handle(userRequest);

    UserState actual = userStateService.getStateForUser(chatId);
    Assertions.assertTrue(actual.isEmpty());

    verify(cvService, times(1)).getCVByName(eq(cvName));
    verify(cvService, times(1)).updateCV(eq(UpdateCVDTO.builder().isHidden(false).build()));
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
            ToggleOpenCVCommandHandler.ToggleOpenCVState.WAITING_FOR_TOGGLE_CV_NAME,
            new HashMap<>()));
    UserRequest userRequest =
        UserRequest.builder().chatId(chatId).text(cvName).isCommand(false).build();

    toggleOpenCVCommandHandler.handle(userRequest);

    verify(cvService, times(1)).getCVByName(eq(cvName));
    verify(cvService, never()).updateCV(any(UpdateCVDTO.class));
    verify(telegramService, times(1)).sendMessage(eq(chatId), anyString());
  }

  @Test
  public void shouldWaitForCVIdAndToggleCVOpenIfCVExists() {
    String cvId = "cv_id";
    when(cvService.getCVById(anyString()))
        .thenReturn(Optional.of(CV.builder().isHidden(true).build()));
    Long chatId = 1L;
    userStateService.setStateForUser(
        chatId,
        new UserState(
            chatId,
            Command.TOGGLE_CV_OPEN,
            ToggleOpenCVCommandHandler.ToggleOpenCVState.WAITING_FOR_TOGGLE_CV_ID,
            new HashMap<>()));
    UserRequest userRequest =
        UserRequest.builder().chatId(chatId).text(cvId).isCommand(false).build();

    toggleOpenCVCommandHandler.handle(userRequest);

    UserState actual = userStateService.getStateForUser(chatId);
    Assertions.assertTrue(actual.isEmpty());

    verify(cvService, times(2)).getCVById(eq(cvId));
    verify(cvService, times(1)).updateCV(eq(UpdateCVDTO.builder().isHidden(false).build()));
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
            ToggleOpenCVCommandHandler.ToggleOpenCVState.WAITING_FOR_TOGGLE_CV_ID,
            new HashMap<>()));
    UserRequest userRequest =
        UserRequest.builder().chatId(chatId).text(cvId).isCommand(false).build();

    toggleOpenCVCommandHandler.handle(userRequest);

    verify(cvService, times(1)).getCVById(eq(cvId));
    verify(cvService, never()).updateCV(any(UpdateCVDTO.class));
    verify(telegramService, times(1)).sendMessage(eq(chatId), anyString());
  }
}
