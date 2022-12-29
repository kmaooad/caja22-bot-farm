package edu.kmaooad.command.handler.cv;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import edu.kmaooad.command.dispatch.Command;
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

public class UpdateCVCommandHandlerTest {

  private TelegramService telegramService;
  private CVService cvService;
  private UserStateService userStateService;
  private UpdateCVCommandHandler updateCVCommandHandler;

  @BeforeEach
  public void setup() {
    cvService = mock(CVService.class);
    telegramService = mock(TelegramService.class);
    userStateService = new UserStateServiceImpl();
    updateCVCommandHandler =
        new UpdateCVCommandHandler(telegramService, userStateService, cvService);
  }

  @Test
  public void shouldHandleUpdateCVCommand() {
    Assertions.assertTrue(updateCVCommandHandler.canHandle(Command.UPDATE_CV));
  }

  @Test
  public void shouldInitializeStateIfCurrentStateIsEmpty() {
    Long chatId = 1L;
    userStateService.setStateForUser(chatId, UserState.newEmptyState(chatId));
    UserRequest userRequest =
        UserRequest.builder().chatId(chatId).text("/updateCV").isCommand(true).build();

    updateCVCommandHandler.handle(userRequest);

    UserState actual = userStateService.getStateForUser(chatId);
    Assertions.assertEquals(actual.getCurrentCommand(), Command.UPDATE_CV);
    Assertions.assertEquals(
        actual.getCommandState(),
        UpdateCVCommandHandler.UpdateCVState.WAITING_FOR_UPDATE_CV_DECISION);

    verify(telegramService, times(1))
        .sendMessage(
            eq(chatId),
            eq(UpdateCVCommandHandler.UpdateCVState.WAITING_FOR_UPDATE_CV_DECISION.getMessage()));
  }

  @Test
  public void shouldWaitForUpdateCVDecisionAndMoveToUpdatingByNameIfNameOptionSpecified() {
    Long chatId = 1L;
    userStateService.setStateForUser(
        chatId,
        new UserState(
            chatId,
            Command.UPDATE_CV,
            UpdateCVCommandHandler.UpdateCVState.WAITING_FOR_UPDATE_CV_DECISION,
            new HashMap<>()));
    UserRequest userRequest =
        UserRequest.builder().chatId(chatId).text("Y").isCommand(false).build();

    updateCVCommandHandler.handle(userRequest);

    UserState actual = userStateService.getStateForUser(chatId);
    Assertions.assertEquals(actual.getCurrentCommand(), Command.UPDATE_CV);
    Assertions.assertEquals(
        actual.getCommandState(),
        UpdateCVCommandHandler.UpdateCVState.WAITING_FOR_UPDATE_CV_ID_NAME);

    verify(telegramService, times(1)).sendMessage(eq(chatId), anyString());
  }

  @Test
  public void shouldWaitForUpdateCVDecisionAndMoveToUpdatingByIdIfIdOptionSpecified() {
    Long chatId = 1L;
    userStateService.setStateForUser(
        chatId,
        new UserState(
            chatId,
            Command.UPDATE_CV,
            UpdateCVCommandHandler.UpdateCVState.WAITING_FOR_UPDATE_CV_DECISION,
            new HashMap<>()));
    UserRequest userRequest =
        UserRequest.builder().chatId(chatId).text("N").isCommand(false).build();

    updateCVCommandHandler.handle(userRequest);

    UserState actual = userStateService.getStateForUser(chatId);
    Assertions.assertEquals(actual.getCurrentCommand(), Command.UPDATE_CV);
    Assertions.assertEquals(
        actual.getCommandState(), UpdateCVCommandHandler.UpdateCVState.WAITING_FOR_UPDATE_CV_ID);

    verify(telegramService, times(1)).sendMessage(eq(chatId), anyString());
  }

  @Test
  public void shouldWaitForIdNameAndMoveToNextStateIfCVExists() {
    when(cvService.getCVByName(anyString())).thenReturn(Optional.of(CV.builder().build()));
    Long chatId = 1L;
    userStateService.setStateForUser(
        chatId,
        new UserState(
            chatId,
            Command.UPDATE_CV,
            UpdateCVCommandHandler.UpdateCVState.WAITING_FOR_UPDATE_CV_ID_NAME,
            new HashMap<>()));
    UserRequest userRequest =
        UserRequest.builder().chatId(chatId).text("some_name").isCommand(false).build();

    updateCVCommandHandler.handle(userRequest);

    UserState actual = userStateService.getStateForUser(chatId);
    Assertions.assertEquals(actual.getCurrentCommand(), Command.UPDATE_CV);
    Assertions.assertEquals(
        actual.getCommandState(), UpdateCVCommandHandler.UpdateCVState.WAITING_FOR_UPDATE_NAME);

    verify(cvService, times(1)).getCVByName(eq("some_name"));
    verify(telegramService, times(1)).sendMessage(eq(chatId), anyString());
  }

  @Test
  public void shouldWaitForIdNameAndSendWarningMessageIfCVDoesNotExist() {
    when(cvService.getCVByName(anyString())).thenReturn(Optional.empty());
    Long chatId = 1L;
    userStateService.setStateForUser(
        chatId,
        new UserState(
            chatId,
            Command.UPDATE_CV,
            UpdateCVCommandHandler.UpdateCVState.WAITING_FOR_UPDATE_CV_ID_NAME,
            new HashMap<>()));
    UserRequest userRequest =
        UserRequest.builder().chatId(chatId).text("some_name").isCommand(false).build();

    updateCVCommandHandler.handle(userRequest);

    UserState actual = userStateService.getStateForUser(chatId);
    Assertions.assertEquals(actual.getCurrentCommand(), Command.UPDATE_CV);
    Assertions.assertEquals(
        actual.getCommandState(),
        UpdateCVCommandHandler.UpdateCVState.WAITING_FOR_UPDATE_CV_ID_NAME);

    verify(cvService, times(1)).getCVByName(eq("some_name"));
    verify(telegramService, times(1)).sendMessage(eq(chatId), anyString());
  }

  @Test
  public void shouldWaitForIdAndMoveToNextStateIfCVExists() {
    when(cvService.getCVById(anyString())).thenReturn(Optional.of(CV.builder().build()));
    Long chatId = 1L;
    userStateService.setStateForUser(
        chatId,
        new UserState(
            chatId,
            Command.UPDATE_CV,
            UpdateCVCommandHandler.UpdateCVState.WAITING_FOR_UPDATE_CV_ID,
            new HashMap<>()));
    UserRequest userRequest =
        UserRequest.builder().chatId(chatId).text("cv_id").isCommand(false).build();

    updateCVCommandHandler.handle(userRequest);

    UserState actual = userStateService.getStateForUser(chatId);
    Assertions.assertEquals(actual.getCurrentCommand(), Command.UPDATE_CV);
    Assertions.assertEquals(
        actual.getCommandState(), UpdateCVCommandHandler.UpdateCVState.WAITING_FOR_UPDATE_NAME);

    verify(cvService, times(1)).getCVById(eq("cv_id"));
    verify(telegramService, times(1)).sendMessage(eq(chatId), anyString());
  }

  @Test
  public void shouldWaitForIdAndSendWarningMessageIfCVDoesNotExist() {
    when(cvService.getCVByName(anyString())).thenReturn(Optional.empty());
    Long chatId = 1L;
    userStateService.setStateForUser(
        chatId,
        new UserState(
            chatId,
            Command.UPDATE_CV,
            UpdateCVCommandHandler.UpdateCVState.WAITING_FOR_UPDATE_CV_ID,
            new HashMap<>()));
    UserRequest userRequest =
        UserRequest.builder().chatId(chatId).text("cv_id").isCommand(false).build();

    updateCVCommandHandler.handle(userRequest);

    UserState actual = userStateService.getStateForUser(chatId);
    Assertions.assertEquals(actual.getCurrentCommand(), Command.UPDATE_CV);
    Assertions.assertEquals(
        actual.getCommandState(), UpdateCVCommandHandler.UpdateCVState.WAITING_FOR_UPDATE_CV_ID);

    verify(cvService, times(1)).getCVById(eq("cv_id"));
    verify(telegramService, times(1)).sendMessage(eq(chatId), anyString());
  }

  @Test
  public void shouldWaitForNameAndMoveToNextState() {
    Long chatId = 1L;
    userStateService.setStateForUser(
        chatId,
        new UserState(
            chatId,
            Command.UPDATE_CV,
            UpdateCVCommandHandler.UpdateCVState.WAITING_FOR_UPDATE_NAME,
            new HashMap<>()));
    UserRequest userRequest =
        UserRequest.builder().chatId(chatId).text("some_name").isCommand(false).build();

    updateCVCommandHandler.handle(userRequest);

    UserState actual = userStateService.getStateForUser(chatId);
    Assertions.assertEquals(actual.getCurrentCommand(), Command.UPDATE_CV);
    Assertions.assertEquals(
        actual.getCommandState(),
        UpdateCVCommandHandler.UpdateCVState.WAITING_FOR_UPDATE_DESCRIPTION);
    Assertions.assertTrue(actual.getInputs().containsKey("name"));
    Assertions.assertEquals(actual.getInputs().get("name"), "some_name");

    verify(telegramService, times(1))
        .sendMessage(
            eq(chatId),
            eq(UpdateCVCommandHandler.UpdateCVState.WAITING_FOR_UPDATE_DESCRIPTION.getMessage()));
  }

  @Test
  public void shouldWaitForDescriptionAndMoveToNextState() {
    Long chatId = 1L;
    userStateService.setStateForUser(
        chatId,
        new UserState(
            chatId,
            Command.UPDATE_CV,
            UpdateCVCommandHandler.UpdateCVState.WAITING_FOR_UPDATE_DESCRIPTION,
            new HashMap<>()));
    UserRequest userRequest =
        UserRequest.builder().chatId(chatId).text("some_description").isCommand(false).build();

    updateCVCommandHandler.handle(userRequest);

    UserState actual = userStateService.getStateForUser(chatId);
    Assertions.assertEquals(actual.getCurrentCommand(), Command.UPDATE_CV);
    Assertions.assertEquals(
        actual.getCommandState(),
        UpdateCVCommandHandler.UpdateCVState.WAITING_FOR_UPDATE_ACTIVITIES);
    Assertions.assertTrue(actual.getInputs().containsKey("description"));
    Assertions.assertEquals(actual.getInputs().get("description"), "some_description");

    verify(telegramService, times(1))
        .sendMessage(
            eq(chatId),
            eq(UpdateCVCommandHandler.UpdateCVState.WAITING_FOR_UPDATE_ACTIVITIES.getMessage()));
  }

  @Test
  public void shouldWaitForActivitiesAndMoveToNextState() {
    Long chatId = 1L;
    userStateService.setStateForUser(
        chatId,
        new UserState(
            chatId,
            Command.UPDATE_CV,
            UpdateCVCommandHandler.UpdateCVState.WAITING_FOR_UPDATE_ACTIVITIES,
            new HashMap<>()));
    UserRequest userRequest =
        UserRequest.builder().chatId(chatId).text("some_activities").isCommand(false).build();

    updateCVCommandHandler.handle(userRequest);

    UserState actual = userStateService.getStateForUser(chatId);
    Assertions.assertEquals(actual.getCurrentCommand(), Command.UPDATE_CV);
    Assertions.assertEquals(
        actual.getCommandState(),
        UpdateCVCommandHandler.UpdateCVState.WAITING_FOR_UPDATE_MANAGE_COMPETENCIES);
    Assertions.assertTrue(actual.getInputs().containsKey("activities"));
    Assertions.assertEquals(actual.getInputs().get("activities"), "some_activities");

    verify(telegramService, times(1))
        .sendMessage(
            eq(chatId),
            eq(
                UpdateCVCommandHandler.UpdateCVState.WAITING_FOR_UPDATE_MANAGE_COMPETENCIES
                    .getMessage()));
  }

  @Test
  public void shouldSkipCompetencesAndMoveToNextState() {
    Long chatId = 1L;
    userStateService.setStateForUser(
        chatId,
        new UserState(
            chatId,
            Command.UPDATE_CV,
            UpdateCVCommandHandler.UpdateCVState.WAITING_FOR_UPDATE_MANAGE_COMPETENCIES,
            new HashMap<>()));
    UserRequest userRequest =
        UserRequest.builder().chatId(chatId).text("Y").isCommand(false).build();

    updateCVCommandHandler.handle(userRequest);

    UserState actual = userStateService.getStateForUser(chatId);
    Assertions.assertEquals(actual.getCurrentCommand(), Command.UPDATE_CV);
    Assertions.assertEquals(
        actual.getCommandState(),
        UpdateCVCommandHandler.UpdateCVState.WAITING_FOR_UPDATE_PREFERENCES);
    Assertions.assertTrue(actual.getInputs().containsKey("manageCompetencies"));
    Assertions.assertEquals(actual.getInputs().get("manageCompetencies"), "Y");

    verify(telegramService, times(1))
        .sendMessage(
            eq(chatId),
            eq(UpdateCVCommandHandler.UpdateCVState.WAITING_FOR_UPDATE_PREFERENCES.getMessage()));
  }

  @Test
  public void shouldNotSkipCompetencesAndMoveToNextState() {
    Long chatId = 1L;
    userStateService.setStateForUser(
        chatId,
        new UserState(
            chatId,
            Command.UPDATE_CV,
            UpdateCVCommandHandler.UpdateCVState.WAITING_FOR_UPDATE_MANAGE_COMPETENCIES,
            new HashMap<>()));
    UserRequest userRequest =
        UserRequest.builder().chatId(chatId).text("N").isCommand(false).build();

    updateCVCommandHandler.handle(userRequest);

    UserState actual = userStateService.getStateForUser(chatId);
    Assertions.assertEquals(actual.getCurrentCommand(), Command.UPDATE_CV);
    Assertions.assertEquals(
        actual.getCommandState(),
        UpdateCVCommandHandler.UpdateCVState.WAITING_FOR_UPDATE_COMPETENCES);
    Assertions.assertTrue(actual.getInputs().containsKey("manageCompetencies"));
    Assertions.assertEquals(actual.getInputs().get("manageCompetencies"), "N");

    verify(telegramService, times(1))
        .sendMessage(
            eq(chatId),
            eq(UpdateCVCommandHandler.UpdateCVState.WAITING_FOR_UPDATE_COMPETENCES.getMessage()));
  }

  @Test
  public void shouldWaitForCompetencesAndMoveToNextState() {
    Long chatId = 1L;
    userStateService.setStateForUser(
        chatId,
        new UserState(
            chatId,
            Command.UPDATE_CV,
            UpdateCVCommandHandler.UpdateCVState.WAITING_FOR_UPDATE_COMPETENCES,
            new HashMap<>()));
    UserRequest userRequest =
        UserRequest.builder().chatId(chatId).text("some_competencies").isCommand(false).build();

    updateCVCommandHandler.handle(userRequest);

    UserState actual = userStateService.getStateForUser(chatId);
    Assertions.assertEquals(actual.getCurrentCommand(), Command.UPDATE_CV);
    Assertions.assertEquals(
        actual.getCommandState(),
        UpdateCVCommandHandler.UpdateCVState.WAITING_FOR_UPDATE_PREFERENCES);
    Assertions.assertTrue(actual.getInputs().containsKey("competences"));
    Assertions.assertEquals(actual.getInputs().get("competences"), "some_competencies");

    verify(telegramService, times(1))
        .sendMessage(
            eq(chatId),
            eq(UpdateCVCommandHandler.UpdateCVState.WAITING_FOR_UPDATE_PREFERENCES.getMessage()));
  }

  @Test
  public void shouldWaitForPreferencesAndMoveToNextState() {
    Long chatId = 1L;
    userStateService.setStateForUser(
        chatId,
        new UserState(
            chatId,
            Command.UPDATE_CV,
            UpdateCVCommandHandler.UpdateCVState.WAITING_FOR_UPDATE_PREFERENCES,
            new HashMap<>()));
    UserRequest userRequest =
        UserRequest.builder().chatId(chatId).text("some_preferences").isCommand(false).build();

    updateCVCommandHandler.handle(userRequest);

    UserState actual = userStateService.getStateForUser(chatId);
    Assertions.assertEquals(actual.getCurrentCommand(), Command.UPDATE_CV);
    Assertions.assertEquals(
        actual.getCommandState(),
        UpdateCVCommandHandler.UpdateCVState.WAITING_FOR_UPDATE_IS_HIDDEN);
    Assertions.assertTrue(actual.getInputs().containsKey("preferences"));
    Assertions.assertEquals(actual.getInputs().get("preferences"), "some_preferences");

    verify(telegramService, times(1))
        .sendMessage(
            eq(chatId),
            eq(UpdateCVCommandHandler.UpdateCVState.WAITING_FOR_UPDATE_IS_HIDDEN.getMessage()));
  }

  @Test
  public void shouldWaitForIsHiddenAndMoveToNextState() {
    Long chatId = 1L;
    userStateService.setStateForUser(
        chatId,
        new UserState(
            chatId,
            Command.UPDATE_CV,
            UpdateCVCommandHandler.UpdateCVState.WAITING_FOR_UPDATE_IS_HIDDEN,
            new HashMap<>()));
    UserRequest userRequest =
        UserRequest.builder().chatId(chatId).text("Y").isCommand(false).build();

    updateCVCommandHandler.handle(userRequest);

    UserState actual = userStateService.getStateForUser(chatId);
    Assertions.assertEquals(actual.getCurrentCommand(), Command.UPDATE_CV);
    Assertions.assertEquals(
        actual.getCommandState(),
        UpdateCVCommandHandler.UpdateCVState.WAITING_FOR_UPDATE_IS_ACTIVE);
    Assertions.assertTrue(actual.getInputs().containsKey("isHidden"));
    Assertions.assertEquals(actual.getInputs().get("isHidden"), "Y");

    verify(telegramService, times(1))
        .sendMessage(
            eq(chatId),
            eq(UpdateCVCommandHandler.UpdateCVState.WAITING_FOR_UPDATE_IS_ACTIVE.getMessage()));
  }
}
