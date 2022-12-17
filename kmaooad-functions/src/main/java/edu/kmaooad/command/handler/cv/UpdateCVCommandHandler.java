package edu.kmaooad.command.handler.cv;

import edu.kmaooad.command.dispatch.Command;
import edu.kmaooad.command.handler.CommandHandler;
import edu.kmaooad.command.handler.CommandState;
import edu.kmaooad.domain.dto.cv.UpdateCVDTO;
import edu.kmaooad.domain.model.CV;
import edu.kmaooad.domain.model.CompetenceCenter;
import edu.kmaooad.domain.model.UserRequest;
import edu.kmaooad.domain.model.UserState;
import edu.kmaooad.service.CVService;
import edu.kmaooad.service.TelegramService;
import edu.kmaooad.service.UserStateService;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UpdateCVCommandHandler implements CommandHandler {

  private enum UpdateCVState implements CommandState {
    WAITING_FOR_UPDATE_CV_DECISION(
        "TOGGLE_CV_WAITING_FOR_DECISION", "You want to update your CV? (Y or N)"),
    WAITING_FOR_UPDATE_CV_ID(
        "TOGGLE_CV_WAITING_FOR_ID", "Please, enter an id of the cv which you want to updates"),
    WAITING_FOR_UPDATE_CV_ID_NAME(
        "TOGGLE_CV_WAITING_FOR_ID", "Please, enter you full name (must not be empty) to start"),
    WAITING_FOR_UPDATE_NAME(
        "ADD_CV_WAITING_FOR_NAME",
        "Please, enter you full name (must not be empty) or 'skip' to skip this field"),
    WAITING_FOR_UPDATE_DESCRIPTION(
        "ADD_CV_WAITING_FOR_DESCRIPTION",
        "Please, enter short summary (can be empty) or 'skip' to skip this field"),
    WAITING_FOR_UPDATE_ACTIVITIES(
        "ADD_CV_WAITING_FOR_ACTIVITIES",
        "Please, enter a list of job activities (as comma-separated list) or 'skip' to skip this"
            + " field"),
    WAITING_FOR_UPDATE_MANAGE_COMPETENCIES(
        "ADD_CV_WAITING_FOR_MANAGE_COMPETENCIES",
        "Should your competencies be filled automatically? (Y or N) or 'skip' to skip this field"),
    WAITING_FOR_UPDATE_COMPETENCES(
        "ADD_CV_WAITING_FOR_COMPETENCES",
        "Please, enter a list of you job competences (as comma-separated list) or 'skip' to skip"
            + " this field"),
    WAITING_FOR_UPDATE_PREFERENCES(
        "ADD_CV_WAITING_FOR_PREFERENCES",
        "Please, enter a list of you job preferences (as comma-separated list) or 'skip' to skip"
            + " this field"),
    WAITING_FOR_UPDATE_IS_HIDDEN(
        "ADD_CV_WAITING_FOR_IS_HIDDEN", "Hide your CV? (Y or N) or 'skip' to skip this field"),
    WAITING_FOR_UPDATE_IS_ACTIVE(
        "ADD_CV_WAITING_FOR_IS_ACTIVE",
        "Are you open to new opportunities? (Y or N) or 'skip' to skip this field");

    private final String name;
    private final String message;

    UpdateCVState(String name, String message) {
      this.name = name;
      this.message = message;
    }

    @Override
    public String getName() {
      return name;
    }

    public String getMessage() {
      return message;
    }
  }

  private final TelegramService telegramService;
  private final UserStateService userStateService;
  private final CVService cvService;

  @Override
  public void handle(UserRequest userRequest) {
    final UserState userState = userStateService.getStateForUser(userRequest.getChatId());
    if (userState.isEmpty()) {
      initializeUserState(userRequest, userState);
    } else {
      performStateAction(userRequest, userState);
    }
  }

  private void performStateAction(UserRequest userRequest, UserState userState) {
    final UpdateCVCommandHandler.UpdateCVState currentState =
        (UpdateCVCommandHandler.UpdateCVState) userState.getCommandState();
    final String userInput = userRequest.getText();
    final Long chatId = userRequest.getChatId();

    switch (currentState) {
      case WAITING_FOR_UPDATE_CV_DECISION:
        userState.addInput("decision", userInput);
        UpdateCVCommandHandler.UpdateCVState nextState =
            Objects.equals(userInput, "Y")
                ? UpdateCVState.WAITING_FOR_UPDATE_CV_ID_NAME
                : UpdateCVState.WAITING_FOR_UPDATE_CV_ID;
        userState.setCommandState(nextState);
        telegramService.sendMessage(chatId, nextState.getMessage());
        userStateService.setStateForUser(chatId, userState);
        break;

      case WAITING_FOR_UPDATE_CV_ID:
        if (cvService.getCVById(userInput).isPresent()) {
          userState.addInput("id", userInput);
          userState.setCommandState(UpdateCVCommandHandler.UpdateCVState.WAITING_FOR_UPDATE_NAME);
          telegramService.sendMessage(
              chatId, UpdateCVCommandHandler.UpdateCVState.WAITING_FOR_UPDATE_NAME.getMessage());
          userStateService.setStateForUser(chatId, userState);
        } else {
          telegramService.sendMessage(chatId, "CV with provided id doesn't exist!");
        }
        break;

      case WAITING_FOR_UPDATE_CV_ID_NAME:
        if (cvService.getCVByName(userInput).isPresent()) {
          userState.addInput("idname", userInput);
          userState.setCommandState(UpdateCVCommandHandler.UpdateCVState.WAITING_FOR_UPDATE_NAME);
          telegramService.sendMessage(
              chatId, UpdateCVCommandHandler.UpdateCVState.WAITING_FOR_UPDATE_NAME.getMessage());
          userStateService.setStateForUser(chatId, userState);
        } else {
          telegramService.sendMessage(chatId, "CV with provided full name doesn't exist!");
        }
        break;

      case WAITING_FOR_UPDATE_NAME:
        userState.addInput("name", userInput);
        userState.setCommandState(
            UpdateCVCommandHandler.UpdateCVState.WAITING_FOR_UPDATE_DESCRIPTION);
        telegramService.sendMessage(
            chatId,
            UpdateCVCommandHandler.UpdateCVState.WAITING_FOR_UPDATE_DESCRIPTION.getMessage());
        userStateService.setStateForUser(chatId, userState);
        break;

      case WAITING_FOR_UPDATE_DESCRIPTION:
        userState.addInput("description", userInput);
        userState.setCommandState(
            UpdateCVCommandHandler.UpdateCVState.WAITING_FOR_UPDATE_ACTIVITIES);
        telegramService.sendMessage(
            chatId,
            UpdateCVCommandHandler.UpdateCVState.WAITING_FOR_UPDATE_ACTIVITIES.getMessage());
        userStateService.setStateForUser(chatId, userState);
        break;

      case WAITING_FOR_UPDATE_ACTIVITIES:
        userState.addInput("activities", userInput);
        userState.setCommandState(
            UpdateCVCommandHandler.UpdateCVState.WAITING_FOR_UPDATE_MANAGE_COMPETENCIES);
        telegramService.sendMessage(
            chatId,
            UpdateCVCommandHandler.UpdateCVState.WAITING_FOR_UPDATE_MANAGE_COMPETENCIES
                .getMessage());
        userStateService.setStateForUser(chatId, userState);
        break;

      case WAITING_FOR_UPDATE_MANAGE_COMPETENCIES:
        userState.addInput("manageCompetencies", userInput);
        UpdateCVState nextState1 =
            Objects.equals(userInput, "Y")
                ? UpdateCVCommandHandler.UpdateCVState.WAITING_FOR_UPDATE_PREFERENCES
                : UpdateCVCommandHandler.UpdateCVState.WAITING_FOR_UPDATE_COMPETENCES;
        userState.setCommandState(nextState1);
        telegramService.sendMessage(chatId, nextState1.getMessage());
        userStateService.setStateForUser(chatId, userState);
        break;

      case WAITING_FOR_UPDATE_COMPETENCES:
        userState.addInput("competences", userInput);
        userState.setCommandState(
            UpdateCVCommandHandler.UpdateCVState.WAITING_FOR_UPDATE_PREFERENCES);
        telegramService.sendMessage(
            chatId,
            UpdateCVCommandHandler.UpdateCVState.WAITING_FOR_UPDATE_PREFERENCES.getMessage());
        userStateService.setStateForUser(chatId, userState);
        break;

      case WAITING_FOR_UPDATE_PREFERENCES:
        userState.addInput("preferences", userInput);
        userState.setCommandState(
            UpdateCVCommandHandler.UpdateCVState.WAITING_FOR_UPDATE_IS_HIDDEN);
        telegramService.sendMessage(
            chatId, UpdateCVCommandHandler.UpdateCVState.WAITING_FOR_UPDATE_IS_HIDDEN.getMessage());
        userStateService.setStateForUser(chatId, userState);
        break;

      case WAITING_FOR_UPDATE_IS_HIDDEN:
        userState.addInput("isHidden", userInput);
        userState.setCommandState(
            UpdateCVCommandHandler.UpdateCVState.WAITING_FOR_UPDATE_IS_ACTIVE);
        telegramService.sendMessage(
            chatId, UpdateCVCommandHandler.UpdateCVState.WAITING_FOR_UPDATE_IS_ACTIVE.getMessage());
        userStateService.setStateForUser(chatId, userState);
        break;

      case WAITING_FOR_UPDATE_IS_ACTIVE:
        userState.addInput("isActive", userInput);
        final Map<String, String> inputs = userState.getInputs();

        CompetenceCenter competenceCenter = new CompetenceCenter();
        List<String> activities = List.of(inputs.get("activities"));
        final CV cv =
            Objects.equals(inputs.get("decision"), "Y")
                ? cvService.getCVByName(inputs.get("idname")).get()
                : cvService.getCVById(inputs.get("id")).get();
        final UpdateCVDTO updateCVDTO =
            UpdateCVDTO.builder()
                .id(cv.getId())
                .name(
                    Objects.equals(inputs.get("name"), "skip") ? cv.getName() : inputs.get("name"))
                .description(
                    Objects.equals(inputs.get("description"), "skip")
                        ? cv.getDescription()
                        : inputs.get("description"))
                .activities(
                    Objects.equals(inputs.get("activities"), "skip")
                        ? cv.getActivities()
                        : activities)
                .competences(
                    Objects.equals(inputs.get("competences"), "skip")
                        ? cv.getCompetences()
                        : (inputs.get("competences") != null
                            ? List.of(inputs.get("competences"))
                            : competenceCenter.generateCompetencies(activities)))
                .preferences(
                    Objects.equals(inputs.get("preferences"), "skip")
                        ? cv.getPreferences()
                        : List.of(inputs.get("preferences")))
                .isActive(
                    Objects.equals(inputs.get("isActive"), "skip")
                        ? cv.getIsActive()
                        : (Objects.equals(inputs.get("isActive"), "Y")))
                .isHidden(
                    Objects.equals(inputs.get("isHidden"), "skip")
                        ? cv.getIsActive()
                        : (Objects.equals(inputs.get("isHidden"), "Y")))
                .manageCompetencies(
                    Objects.equals(inputs.get("manageCompetencies"), "skip")
                        ? cv.getManageCompetencies()
                        : Objects.equals(inputs.get("manageCompetencies"), "Y"))
                .build();
        cvService.updateCV(updateCVDTO);
        userState.clear();
        userStateService.setStateForUser(chatId, userState);
        telegramService.sendMessage(chatId, "Successfully updated CV!");
        userState.clear();
        userStateService.setStateForUser(chatId, userState);
        break;
    }
  }

  private void initializeUserState(UserRequest userRequest, UserState userState) {
    final Long chatId = userRequest.getChatId();
    userState.setCurrentCommand(Command.UPDATE_CV);
    userState.setCommandState(UpdateCVState.WAITING_FOR_UPDATE_CV_DECISION);
    userStateService.setStateForUser(chatId, userState);
    telegramService.sendMessage(
        chatId, UpdateCVCommandHandler.UpdateCVState.WAITING_FOR_UPDATE_CV_DECISION.getMessage());
  }

  @Override
  public boolean canHandle(Command command) {
    return command.equals(Command.UPDATE_CV);
  }
}
