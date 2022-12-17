package edu.kmaooad.command.handler.cv;

import edu.kmaooad.command.dispatch.Command;
import edu.kmaooad.command.handler.CommandHandler;
import edu.kmaooad.command.handler.CommandState;
import edu.kmaooad.domain.dto.cv.UpdateCVDTO;
import edu.kmaooad.domain.model.CV;
import edu.kmaooad.domain.model.UserRequest;
import edu.kmaooad.domain.model.UserState;
import edu.kmaooad.service.CVService;
import edu.kmaooad.service.TelegramService;
import edu.kmaooad.service.UserStateService;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UpdateCVCompetencesCommandHandler implements CommandHandler {

  private enum UpdateCVCompetencesState implements CommandState {
    WAITING_FOR_UPDATE_COMPETENCES_CV_DECISION(
        "UPDATE_COMPETENCES_CV_WAITING_FOR_DECISION",
        "You want to update competences for your CV? (Y or N)"),
    WAITING_FOR_UPDATE_COMPETENCES_CV_ID(
        "UPDATE_COMPETENCES_CV_WAITING_FOR_ID",
        "Please, enter an id of the cv which you want to update competences"),
    WAITING_FOR_UPDATE_COMPETENCES_CV_NAME(
        "UPDATE_COMPETENCES_CV_WAITING_FOR_NAME", "Please, enter your full name"),
    WAITING_FOR_UPDATE_COMPETENCES_CV_ACTION(
        "UPDATE_COMPETENCES_CV_WAITING_FOR_ACTION",
        "You want to add competences or remove? (+ or -)"),
    WAITING_FOR_UPDATE_COMPETENCES_ADD(
        "UPDATE_COMPETENCES_CV_WAITING_FOR_ADD",
        "Please, enter needed competences (comma-separated list)"),
    WAITING_FOR_UPDATE_COMPETENCES_REMOVE(
        "UPDATE_COMPETENCES_CV_WAITING_FOR_REMOVE",
        "Please, enter needed competences (comma-separated list)");

    private final String name;
    private final String message;

    UpdateCVCompetencesState(String name, String message) {
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
    final UpdateCVCompetencesState currentState =
        (UpdateCVCompetencesState) userState.getCommandState();
    final String userInput = userRequest.getText();
    final Long chatId = userRequest.getChatId();

    final CV cv;
    final UpdateCVDTO updateCVDTO;
    final Map<String, String> inputs;

    switch (currentState) {
      case WAITING_FOR_UPDATE_COMPETENCES_CV_DECISION:
        userState.addInput("decision", userInput);
        UpdateCVCompetencesCommandHandler.UpdateCVCompetencesState nextState =
            Objects.equals(userInput, "Y")
                ? UpdateCVCompetencesState.WAITING_FOR_UPDATE_COMPETENCES_CV_NAME
                : UpdateCVCompetencesState.WAITING_FOR_UPDATE_COMPETENCES_CV_ID;
        userState.setCommandState(nextState);
        telegramService.sendMessage(chatId, nextState.getMessage());
        userStateService.setStateForUser(chatId, userState);
        break;

      case WAITING_FOR_UPDATE_COMPETENCES_CV_ID:
        userState.addInput("id", userInput);
        if (cvService.getCVById(userInput).isPresent()) {
          cv = cvService.getCVById(userInput).get();

          userState.setCommandState(
              UpdateCVCompetencesState.WAITING_FOR_UPDATE_COMPETENCES_CV_ACTION);
          telegramService.sendMessage(
              chatId,
              UpdateCVCompetencesState.WAITING_FOR_UPDATE_COMPETENCES_CV_ACTION.getMessage()
                  + "Your competences:\n\n"
                  + cv.getCompetences());
          userStateService.setStateForUser(chatId, userState);
        } else {
          telegramService.sendMessage(
              chatId, "CV with provided id doesn't exists! Please, provide a valid id");
        }
        break;

      case WAITING_FOR_UPDATE_COMPETENCES_CV_NAME:
        userState.addInput("nameid", userInput);
        if (cvService.getCVByName(userInput).isPresent()) {
          cv = cvService.getCVByName(userInput).get();

          userState.setCommandState(
              UpdateCVCompetencesState.WAITING_FOR_UPDATE_COMPETENCES_CV_ACTION);
          telegramService.sendMessage(
              chatId,
              UpdateCVCompetencesState.WAITING_FOR_UPDATE_COMPETENCES_CV_ACTION.getMessage()
                  + "Your competences:\n\n"
                  + cv.getCompetences());
          userStateService.setStateForUser(chatId, userState);
        } else {
          telegramService.sendMessage(
              chatId, "CV with provided name doesn't exists! Please, provide a valid name");
        }
        break;

      case WAITING_FOR_UPDATE_COMPETENCES_CV_ACTION:
        userState.addInput("action", userInput);
        nextState =
            Objects.equals(userInput, "A")
                ? UpdateCVCompetencesState.WAITING_FOR_UPDATE_COMPETENCES_ADD
                : UpdateCVCompetencesState.WAITING_FOR_UPDATE_COMPETENCES_REMOVE;
        userState.setCommandState(nextState);
        telegramService.sendMessage(chatId, nextState.getMessage());
        userStateService.setStateForUser(chatId, userState);
        break;

      case WAITING_FOR_UPDATE_COMPETENCES_ADD:
        userState.addInput("add", userInput);
        inputs = userState.getInputs();
        if (cvService.getCVByName(inputs.get("idname")).isPresent()) {
          cv = cvService.getCVByName(userInput).get();
        } else {
          cv = cvService.getCVById(inputs.get("id")).get();
        }
        List<String> competences =
            Stream.concat(cv.getCompetences().stream(), Stream.of(inputs.get("add")))
                .collect(Collectors.toList());
        updateCVDTO =
            UpdateCVDTO.builder()
                .id(cv.getId())
                .name(cv.getName())
                .description(cv.getDescription())
                .activities(cv.getActivities())
                .competences(competences)
                .preferences(cv.getPreferences())
                .isActive(cv.getIsActive())
                .manageCompetencies(cv.getManageCompetencies())
                .build();
        cvService.updateCV(updateCVDTO);
        userState.clear();
        userStateService.setStateForUser(chatId, userState);
        telegramService.sendMessage(chatId, "Successfully updated CV!");
        break;

      case WAITING_FOR_UPDATE_COMPETENCES_REMOVE:
        userState.addInput("remove", userInput);
        inputs = userState.getInputs();
        if (cvService.getCVByName(inputs.get("idname")).isPresent()) {
          cv = cvService.getCVByName(userInput).get();
        } else {
          cv = cvService.getCVById(inputs.get("id")).get();
        }
        List<String> cvcompetences = cv.getCompetences();
        cvcompetences.removeAll(List.of(inputs.get("remove")));
        updateCVDTO =
            UpdateCVDTO.builder()
                .id(cv.getId())
                .name(cv.getName())
                .description(cv.getDescription())
                .activities(cv.getActivities())
                .competences(cvcompetences)
                .preferences(cv.getPreferences())
                .isActive(cv.getIsActive())
                .manageCompetencies(cv.getManageCompetencies())
                .build();
        cvService.updateCV(updateCVDTO);
        userState.clear();
        userStateService.setStateForUser(chatId, userState);
        telegramService.sendMessage(chatId, "Successfully updated CV!");
        break;
    }
  }

  private void initializeUserState(UserRequest userRequest, UserState userState) {
    final Long chatId = userRequest.getChatId();
    userState.setCurrentCommand(Command.UPDATE_CV_COMPETENCES);
    userState.setCommandState(UpdateCVCompetencesState.WAITING_FOR_UPDATE_COMPETENCES_CV_DECISION);
    userStateService.setStateForUser(chatId, userState);
    telegramService.sendMessage(
        chatId, UpdateCVCompetencesState.WAITING_FOR_UPDATE_COMPETENCES_CV_DECISION.getMessage());
  }

  @Override
  public boolean canHandle(Command command) {
    return command.equals(Command.UPDATE_CV_COMPETENCES);
  }
}
