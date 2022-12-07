package edu.kmaooad.command.handler;

import edu.kmaooad.command.Command;
import edu.kmaooad.domain.dto.cv.AddCVDTO;
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
public class AddCVCommandHandler implements CommandHandler {

  private enum AddCVState implements CommandState {
    WAITING_FOR_NAME("ADD_CV_WAITING_FOR_NAME", "Please, enter you full name (must not be empty)"),
    WAITING_FOR_DESCRIPTION(
        "ADD_CV_WAITING_FOR_DESCRIPTION", "Please, enter short summary (can be empty)"),
    WAITING_FOR_ACTIVITIES(
        "ADD_CV_WAITING_FOR_ACTIVITIES",
        "Please, enter a list of job activities (as comma-separated list)"),
    WAITING_FOR_MANAGE_COMPETENCIES(
        "ADD_CV_WAITING_FOR_MANAGE_COMPETENCIES",
        "Should your competencies be filled automatically? (Y or N)"),
    WAITING_FOR_COMPETENCES(
        "ADD_CV_WAITING_FOR_COMPETENCES",
        "Please, enter a list of you job competences (as comma-separated list)"),
    WAITING_FOR_PREFERENCES(
        "ADD_CV_WAITING_FOR_PREFERENCES",
        "Please, enter a list of you job preferences (as comma-separated list)"),
    WAITING_FOR_IS_ACTIVE(
        "ADD_CV_WAITING_FOR_IS_ACTIVE", "Are you open to new opportunities? (Y or N)");

    private final String name;
    private final String message;

    AddCVState(String name, String message) {
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
    final AddCVCommandHandler.AddCVState currentState =
        (AddCVCommandHandler.AddCVState) userState.getCommandState();
    final String userInput = userRequest.getText();
    final Long chatId = userRequest.getChatId();

    switch (currentState) {
      case WAITING_FOR_NAME:
        userState.addInput("name", userInput);
        userState.setCommandState(AddCVCommandHandler.AddCVState.WAITING_FOR_DESCRIPTION);
        telegramService.sendMessage(
            chatId, AddCVCommandHandler.AddCVState.WAITING_FOR_DESCRIPTION.getMessage());
        userStateService.setStateForUser(chatId, userState);
        break;

      case WAITING_FOR_DESCRIPTION:
        userState.addInput("description", userInput);
        userState.setCommandState(AddCVCommandHandler.AddCVState.WAITING_FOR_ACTIVITIES);
        telegramService.sendMessage(
            chatId, AddCVCommandHandler.AddCVState.WAITING_FOR_ACTIVITIES.getMessage());
        userStateService.setStateForUser(chatId, userState);
        break;

      case WAITING_FOR_ACTIVITIES:
        userState.addInput("activities", userInput);
        userState.setCommandState(AddCVCommandHandler.AddCVState.WAITING_FOR_MANAGE_COMPETENCIES);
        telegramService.sendMessage(
            chatId, AddCVCommandHandler.AddCVState.WAITING_FOR_MANAGE_COMPETENCIES.getMessage());
        userStateService.setStateForUser(chatId, userState);
        break;

      case WAITING_FOR_MANAGE_COMPETENCIES:
        userState.addInput("manageCompetencies", userInput);
        AddCVState nextState =
            Objects.equals(userInput, "Y")
                ? AddCVCommandHandler.AddCVState.WAITING_FOR_PREFERENCES
                : AddCVCommandHandler.AddCVState.WAITING_FOR_COMPETENCES;
        userState.setCommandState(nextState);
        telegramService.sendMessage(chatId, nextState.getMessage());
        userStateService.setStateForUser(chatId, userState);
        break;

      case WAITING_FOR_COMPETENCES:
        userState.addInput("competences", userInput);
        userState.setCommandState(AddCVCommandHandler.AddCVState.WAITING_FOR_PREFERENCES);
        telegramService.sendMessage(
            chatId, AddCVCommandHandler.AddCVState.WAITING_FOR_PREFERENCES.getMessage());
        userStateService.setStateForUser(chatId, userState);
        break;

      case WAITING_FOR_PREFERENCES:
        userState.addInput("preferences", userInput);
        userState.setCommandState(AddCVCommandHandler.AddCVState.WAITING_FOR_IS_ACTIVE);
        telegramService.sendMessage(
            chatId, AddCVCommandHandler.AddCVState.WAITING_FOR_IS_ACTIVE.getMessage());
        userStateService.setStateForUser(chatId, userState);
        break;

      case WAITING_FOR_IS_ACTIVE:
        userState.addInput("isActive", userInput);
        final Map<String, String> inputs = userState.getInputs();
        final AddCVDTO addCVDTO =
            AddCVDTO.builder()
                .name(inputs.get("name"))
                .description(inputs.get("description"))
                .activities(List.of(inputs.get("activities")))
                .competences(
                    inputs.get("competences") != null
                        ? List.of(inputs.get("competences"))
                        : List.of()) // form list automatically
                .preferences(List.of(inputs.get("preferences"))) // check if not empty!
                .isActive(Objects.equals(inputs.get("isActive"), "Y"))
                .manageCompetencies(Objects.equals(inputs.get("manageCompetencies"), "Y"))
                .build();
        cvService.addCV(addCVDTO);
        telegramService.sendMessage(chatId, "Successfully added your cv!");
        userState.clear();
        userStateService.setStateForUser(chatId, userState);
        break;
    }
  }

  private void initializeUserState(UserRequest userRequest, UserState userState) {
    final Long chatId = userRequest.getChatId();
    userState.setCurrentCommand(Command.ADD_CV);
    userState.setCommandState(AddCVCommandHandler.AddCVState.WAITING_FOR_NAME);
    userStateService.setStateForUser(chatId, userState);
    telegramService.sendMessage(
        chatId, AddCVCommandHandler.AddCVState.WAITING_FOR_NAME.getMessage());
  }

  @Override
  public boolean canHandle(Command command) {
    return command.equals(Command.ADD_CV);
  }
}
