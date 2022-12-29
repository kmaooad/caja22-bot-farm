package edu.kmaooad.command.handler.cv;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import edu.kmaooad.command.dispatch.Command;
import edu.kmaooad.domain.dto.cv.AddCVDTO;
import edu.kmaooad.domain.model.UserState;
import edu.kmaooad.service.CVService;
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

public class AddCVCommandHandlerTest {

  private TelegramService telegramService;
  private CVService cvService;
  private UserStateService userStateService;
  private AddCVCommandHandler addCVCommandHandler;

  @BeforeEach
  public void setup() {
    cvService = mock(CVService.class);
    telegramService = mock(TelegramService.class);
    userStateService = new UserStateServiceImpl();
    addCVCommandHandler = new AddCVCommandHandler(telegramService, userStateService, cvService);
  }

  @Test
  public void shouldHandleAddCVCommand() {
    Assertions.assertTrue(addCVCommandHandler.canHandle(Command.ADD_CV));
  }

  @Test
  public void shouldInitializeStateIfCurrentStateIsEmpty() {
    Long chatId = 1L;
    userStateService.setStateForUser(chatId, UserState.newEmptyState(chatId));
    UserRequest userRequest =
        UserRequest.builder().chatId(chatId).text("/addJob").isCommand(true).build();

    addCVCommandHandler.handle(userRequest);

    UserState actual = userStateService.getStateForUser(chatId);
    Assertions.assertEquals(actual.getCurrentCommand(), Command.ADD_CV);
    Assertions.assertEquals(
        actual.getCommandState(), AddCVCommandHandler.AddCVState.WAITING_FOR_NAME);

    verify(telegramService, times(1))
        .sendMessage(eq(chatId), eq(AddCVCommandHandler.AddCVState.WAITING_FOR_NAME.getMessage()));
  }

  @Test
  public void shouldWaitForNameAndMoveToNextState() {
    Long chatId = 1L;
    userStateService.setStateForUser(
        chatId,
        new UserState(
            chatId,
            Command.ADD_CV,
            AddCVCommandHandler.AddCVState.WAITING_FOR_NAME,
            new HashMap<>()));
    UserRequest userRequest =
        UserRequest.builder().chatId(chatId).text("some_name").isCommand(false).build();

    addCVCommandHandler.handle(userRequest);

    UserState actual = userStateService.getStateForUser(chatId);
    Assertions.assertEquals(actual.getCurrentCommand(), Command.ADD_CV);
    Assertions.assertEquals(
        actual.getCommandState(), AddCVCommandHandler.AddCVState.WAITING_FOR_DESCRIPTION);
    Assertions.assertTrue(actual.getInputs().containsKey("name"));
    Assertions.assertEquals(actual.getInputs().get("name"), "some_name");

    verify(telegramService, times(1))
        .sendMessage(
            eq(chatId), eq(AddCVCommandHandler.AddCVState.WAITING_FOR_DESCRIPTION.getMessage()));
  }

  @Test
  public void shouldWaitForDescriptionAndMoveToNextState() {
    Long chatId = 1L;
    userStateService.setStateForUser(
        chatId,
        new UserState(
            chatId,
            Command.ADD_CV,
            AddCVCommandHandler.AddCVState.WAITING_FOR_DESCRIPTION,
            new HashMap<>()));
    UserRequest userRequest =
        UserRequest.builder().chatId(chatId).text("some_description").isCommand(false).build();

    addCVCommandHandler.handle(userRequest);

    UserState actual = userStateService.getStateForUser(chatId);
    Assertions.assertEquals(actual.getCurrentCommand(), Command.ADD_CV);
    Assertions.assertEquals(
        actual.getCommandState(), AddCVCommandHandler.AddCVState.WAITING_FOR_ACTIVITIES);
    Assertions.assertTrue(actual.getInputs().containsKey("description"));
    Assertions.assertEquals(actual.getInputs().get("description"), "some_description");

    verify(telegramService, times(1))
        .sendMessage(
            eq(chatId), eq(AddCVCommandHandler.AddCVState.WAITING_FOR_ACTIVITIES.getMessage()));
  }

  @Test
  public void shouldWaitForActivitiesAndMoveToNextState() {
    Long chatId = 1L;
    userStateService.setStateForUser(
        chatId,
        new UserState(
            chatId,
            Command.ADD_CV,
            AddCVCommandHandler.AddCVState.WAITING_FOR_ACTIVITIES,
            new HashMap<>()));
    UserRequest userRequest =
        UserRequest.builder().chatId(chatId).text("some_activities").isCommand(false).build();

    addCVCommandHandler.handle(userRequest);

    UserState actual = userStateService.getStateForUser(chatId);
    Assertions.assertEquals(actual.getCurrentCommand(), Command.ADD_CV);
    Assertions.assertEquals(
        actual.getCommandState(), AddCVCommandHandler.AddCVState.WAITING_FOR_MANAGE_COMPETENCIES);
    Assertions.assertTrue(actual.getInputs().containsKey("activities"));
    Assertions.assertEquals(actual.getInputs().get("activities"), "some_activities");

    verify(telegramService, times(1))
        .sendMessage(
            eq(chatId),
            eq(AddCVCommandHandler.AddCVState.WAITING_FOR_MANAGE_COMPETENCIES.getMessage()));
  }

  @Test
  public void shouldSkipCompetencesAndMoveToNextState() {
    Long chatId = 1L;
    userStateService.setStateForUser(
        chatId,
        new UserState(
            chatId,
            Command.ADD_CV,
            AddCVCommandHandler.AddCVState.WAITING_FOR_MANAGE_COMPETENCIES,
            new HashMap<>()));
    UserRequest userRequest =
        UserRequest.builder().chatId(chatId).text("Y").isCommand(false).build();

    addCVCommandHandler.handle(userRequest);

    UserState actual = userStateService.getStateForUser(chatId);
    Assertions.assertEquals(actual.getCurrentCommand(), Command.ADD_CV);
    Assertions.assertEquals(
        actual.getCommandState(), AddCVCommandHandler.AddCVState.WAITING_FOR_PREFERENCES);
    Assertions.assertTrue(actual.getInputs().containsKey("manageCompetencies"));
    Assertions.assertEquals(actual.getInputs().get("manageCompetencies"), "Y");

    verify(telegramService, times(1))
        .sendMessage(
            eq(chatId), eq(AddCVCommandHandler.AddCVState.WAITING_FOR_PREFERENCES.getMessage()));
  }

  @Test
  public void shouldNotSkipCompetencesAndMoveToNextState() {
    Long chatId = 1L;
    userStateService.setStateForUser(
        chatId,
        new UserState(
            chatId,
            Command.ADD_CV,
            AddCVCommandHandler.AddCVState.WAITING_FOR_MANAGE_COMPETENCIES,
            new HashMap<>()));
    UserRequest userRequest =
        UserRequest.builder().chatId(chatId).text("N").isCommand(false).build();

    addCVCommandHandler.handle(userRequest);

    UserState actual = userStateService.getStateForUser(chatId);
    Assertions.assertEquals(actual.getCurrentCommand(), Command.ADD_CV);
    Assertions.assertEquals(
        actual.getCommandState(), AddCVCommandHandler.AddCVState.WAITING_FOR_COMPETENCES);
    Assertions.assertTrue(actual.getInputs().containsKey("manageCompetencies"));
    Assertions.assertEquals(actual.getInputs().get("manageCompetencies"), "N");

    verify(telegramService, times(1))
        .sendMessage(
            eq(chatId), eq(AddCVCommandHandler.AddCVState.WAITING_FOR_COMPETENCES.getMessage()));
  }

  @Test
  public void shouldWaitForCompetencesAndMoveToNextState() {
    Long chatId = 1L;
    userStateService.setStateForUser(
        chatId,
        new UserState(
            chatId,
            Command.ADD_CV,
            AddCVCommandHandler.AddCVState.WAITING_FOR_COMPETENCES,
            new HashMap<>()));
    UserRequest userRequest =
        UserRequest.builder().chatId(chatId).text("some_competencies").isCommand(false).build();

    addCVCommandHandler.handle(userRequest);

    UserState actual = userStateService.getStateForUser(chatId);
    Assertions.assertEquals(actual.getCurrentCommand(), Command.ADD_CV);
    Assertions.assertEquals(
        actual.getCommandState(), AddCVCommandHandler.AddCVState.WAITING_FOR_PREFERENCES);
    Assertions.assertTrue(actual.getInputs().containsKey("competences"));
    Assertions.assertEquals(actual.getInputs().get("competences"), "some_competencies");

    verify(telegramService, times(1))
        .sendMessage(
            eq(chatId), eq(AddCVCommandHandler.AddCVState.WAITING_FOR_PREFERENCES.getMessage()));
  }

  @Test
  public void shouldWaitForPreferencesAndMoveToNextState() {
    Long chatId = 1L;
    userStateService.setStateForUser(
        chatId,
        new UserState(
            chatId,
            Command.ADD_CV,
            AddCVCommandHandler.AddCVState.WAITING_FOR_PREFERENCES,
            new HashMap<>()));
    UserRequest userRequest =
        UserRequest.builder().chatId(chatId).text("some_preferences").isCommand(false).build();

    addCVCommandHandler.handle(userRequest);

    UserState actual = userStateService.getStateForUser(chatId);
    Assertions.assertEquals(actual.getCurrentCommand(), Command.ADD_CV);
    Assertions.assertEquals(
        actual.getCommandState(), AddCVCommandHandler.AddCVState.WAITING_FOR_IS_HIDDEN);
    Assertions.assertTrue(actual.getInputs().containsKey("preferences"));
    Assertions.assertEquals(actual.getInputs().get("preferences"), "some_preferences");

    verify(telegramService, times(1))
        .sendMessage(
            eq(chatId), eq(AddCVCommandHandler.AddCVState.WAITING_FOR_IS_HIDDEN.getMessage()));
  }

  @Test
  public void shouldWaitForIsHiddenAndMoveToNextState() {
    Long chatId = 1L;
    userStateService.setStateForUser(
        chatId,
        new UserState(
            chatId,
            Command.ADD_CV,
            AddCVCommandHandler.AddCVState.WAITING_FOR_IS_HIDDEN,
            new HashMap<>()));
    UserRequest userRequest =
        UserRequest.builder().chatId(chatId).text("Y").isCommand(false).build();

    addCVCommandHandler.handle(userRequest);

    UserState actual = userStateService.getStateForUser(chatId);
    Assertions.assertEquals(actual.getCurrentCommand(), Command.ADD_CV);
    Assertions.assertEquals(
        actual.getCommandState(), AddCVCommandHandler.AddCVState.WAITING_FOR_IS_ACTIVE);
    Assertions.assertTrue(actual.getInputs().containsKey("isHidden"));
    Assertions.assertEquals(actual.getInputs().get("isHidden"), "Y");

    verify(telegramService, times(1))
        .sendMessage(
            eq(chatId), eq(AddCVCommandHandler.AddCVState.WAITING_FOR_IS_ACTIVE.getMessage()));
  }

  @Test
  public void shouldWaitForIsActiveAndAddNewCVWithManualCompetences() {
    Map<String, String> inputs = new HashMap<>();
    String name = "some_name";
    String description = "some_description";
    String activities = "some_activities";
    String competences = "some_competences";
    String preferences = "some_preferences";
    String isActive = "Y";
    String isHidden = "Y";
    String manageCompetences = "N";
    inputs.put("name", name);
    inputs.put("description", description);
    inputs.put("activities", activities);
    inputs.put("competences", competences);
    inputs.put("preferences", preferences);
    inputs.put("isActive", isActive);
    inputs.put("isHidden", isHidden);
    inputs.put("manageCompetencies", manageCompetences);

    Long chatId = 1L;
    userStateService.setStateForUser(
        chatId,
        new UserState(
            chatId, Command.ADD_CV, AddCVCommandHandler.AddCVState.WAITING_FOR_IS_ACTIVE, inputs));
    UserRequest userRequest =
        UserRequest.builder().chatId(chatId).text("Y").isCommand(false).build();

    addCVCommandHandler.handle(userRequest);

    UserState actual = userStateService.getStateForUser(chatId);
    Assertions.assertTrue(actual.isEmpty());

    verify(cvService, times(1))
        .addCV(
            eq(
                AddCVDTO.builder()
                    .name(name)
                    .description(description)
                    .activities(List.of(activities))
                    .competences(List.of(competences))
                    .preferences(List.of(preferences))
                    .isActive(true)
                    .isHidden(true)
                    .manageCompetencies(false)
                    .build()));
    verify(telegramService, times(1)).sendMessage(eq(chatId), anyString());
  }

  @Test
  public void shouldWaitForIsActiveAndAddNewCVWithAutomaticCompetences() {
    Map<String, String> inputs = new HashMap<>();
    String name = "some_name";
    String description = "some_description";
    String activities = "Backend";
    String preferences = "some_preferences";
    String isActive = "Y";
    String isHidden = "Y";
    String manageCompetences = "Y";
    inputs.put("name", name);
    inputs.put("description", description);
    inputs.put("activities", activities);
    inputs.put("preferences", preferences);
    inputs.put("isActive", isActive);
    inputs.put("isHidden", isHidden);
    inputs.put("manageCompetencies", manageCompetences);

    Long chatId = 1L;
    userStateService.setStateForUser(
        chatId,
        new UserState(
            chatId, Command.ADD_CV, AddCVCommandHandler.AddCVState.WAITING_FOR_IS_ACTIVE, inputs));
    UserRequest userRequest =
        UserRequest.builder().chatId(chatId).text("Y").isCommand(false).build();

    addCVCommandHandler.handle(userRequest);

    UserState actual = userStateService.getStateForUser(chatId);
    Assertions.assertTrue(actual.isEmpty());

    verify(cvService, times(1))
        .addCV(
            eq(
                AddCVDTO.builder()
                    .name(name)
                    .description(description)
                    .activities(List.of(activities))
                    .competences(List.of("Java"))
                    .preferences(List.of(preferences))
                    .isActive(true)
                    .isHidden(true)
                    .manageCompetencies(true)
                    .build()));
    verify(telegramService, times(1)).sendMessage(eq(chatId), anyString());
  }
}
