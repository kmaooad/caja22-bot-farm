package edu.kmaooad.command.handler;

import edu.kmaooad.command.Command;
import edu.kmaooad.domain.dto.cv.UpdateCVDTO;
import edu.kmaooad.domain.model.CV;
import edu.kmaooad.domain.model.UserRequest;
import edu.kmaooad.domain.model.UserState;
import edu.kmaooad.service.CVService;
import edu.kmaooad.service.TelegramService;
import edu.kmaooad.service.UserStateService;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ToggleOpenCVCommandHandler implements CommandHandler {

  private enum ToggleHireCVState implements CommandState {
    WAITING_FOR_TOGGLE_CV_DECISION(
        "TOGGLE_CV_WAITING_FOR_DECISION", "You want to toggle hire status for your CV? (Y or N)"),
    WAITING_FOR_TOGGLE_CV_ID(
        "TOGGLE_CV_WAITING_FOR_ID",
        "Please, enter an id of the cv which you want to toggle hire status"),
    WAITING_FOR_TOGGLE_CV_NAME("TOGGLE_CV_WAITING_FOR_NAME", "Please, enter your full name");

    private final String name;
    private final String message;

    ToggleHireCVState(String name, String message) {
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
    final ToggleHireCVState currentState = (ToggleHireCVState) userState.getCommandState();
    final String userInput = userRequest.getText();
    final Long chatId = userRequest.getChatId();

    switch (currentState) {
      case WAITING_FOR_TOGGLE_CV_DECISION:
        userState.addInput("decision", userInput);
        ToggleOpenCVCommandHandler.ToggleHireCVState nextState =
            Objects.equals(userInput, "Y")
                ? ToggleHireCVState.WAITING_FOR_TOGGLE_CV_NAME
                : ToggleHireCVState.WAITING_FOR_TOGGLE_CV_ID;
        userState.setCommandState(nextState);
        telegramService.sendMessage(chatId, nextState.getMessage());
        userStateService.setStateForUser(chatId, userState);
        break;

      case WAITING_FOR_TOGGLE_CV_ID:
        if (cvService.getCVById(userInput).isPresent()) {
          final CV cv = cvService.getCVById(userInput).get();
          final UpdateCVDTO updateCVDTO =
              UpdateCVDTO.builder()
                  .id(cv.getId())
                  .name(cv.getName())
                  .description(cv.getDescription())
                  .activities(cv.getActivities())
                  .competences(cv.getCompetences())
                  .preferences(cv.getPreferences())
                  .isActive(!cv.getIsActive())
                  .manageCompetencies(cv.getManageCompetencies())
                  .build();
          cvService.updateCV(updateCVDTO);
          userState.clear();
          userStateService.setStateForUser(chatId, userState);
          telegramService.sendMessage(chatId, "Successfully updated CV!");
        } else {
          telegramService.sendMessage(
              chatId, "CV with provided id doesn't exists! Please, provide a valid id");
        }
        break;

      case WAITING_FOR_TOGGLE_CV_NAME:
        if (cvService.getCVByName(userInput).isPresent()) {
          final CV cv = cvService.getCVByName(userInput).get();
          final UpdateCVDTO updateCVDTO =
              UpdateCVDTO.builder()
                  .name(cv.getName())
                  .description(cv.getDescription())
                  .activities(cv.getActivities())
                  .competences(cv.getCompetences())
                  .preferences(cv.getPreferences())
                  .isActive(!cv.getIsActive())
                  .isHidden(cv.getIsHidden())
                  .manageCompetencies(cv.getManageCompetencies())
                  .build();
          cvService.updateCV(updateCVDTO);
          userState.clear();
          userStateService.setStateForUser(chatId, userState);
          telegramService.sendMessage(chatId, "Successfully updated CV!");
        } else {
          telegramService.sendMessage(
              chatId, "CV with provided full name doesn't exists! Please, provide a valid id");
        }
        break;
    }
  }

  private void initializeUserState(UserRequest userRequest, UserState userState) {
    final Long chatId = userRequest.getChatId();
    userState.setCurrentCommand(Command.TOGGLE_CV_HIRES);
    userState.setCommandState(ToggleHireCVState.WAITING_FOR_TOGGLE_CV_DECISION);
    userStateService.setStateForUser(chatId, userState);
    telegramService.sendMessage(
        chatId, ToggleHireCVState.WAITING_FOR_TOGGLE_CV_DECISION.getMessage());
  }

  @Override
  public boolean canHandle(Command command) {
    return command.equals(Command.TOGGLE_CV_HIRES);
  }
}
