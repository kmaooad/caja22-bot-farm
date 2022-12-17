package edu.kmaooad.command.handler.job;

import edu.kmaooad.command.dispatch.Command;
import edu.kmaooad.command.handler.CommandHandler;
import edu.kmaooad.command.handler.CommandState;
import edu.kmaooad.domain.dto.job.AddJobDTO;
import edu.kmaooad.domain.model.UserRequest;
import edu.kmaooad.domain.model.UserState;
import edu.kmaooad.service.JobService;
import edu.kmaooad.service.TelegramService;
import edu.kmaooad.service.UserStateService;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AddJobCommandHandler implements CommandHandler {

  private enum AddJobState implements CommandState {
    WAITING_FOR_TITLE("ADD_JOB_WAITING_FOR_TITLE", "Please, enter a job title (must not be empty)"),
    WAITING_FOR_DESCRIPTION(
        "ADD_JOB_WAITING_FOR_DESCRIPTION", "Please, enter a job description (can be empty)"),
    WAITING_FOR_ACTIVITIES(
        "ADD_JOB_WAITING_FOR_ACTIVITIES",
        "Please, enter a list of job activities (as comma-separated list)"),
    WAITING_FOR_COMPETENCES(
        "ADD_JOB_WAITING_FOR_COMPETENCES",
        "Please, enter a list of job competences (as comma-separated list)");

    private final String name;
    private final String message;

    AddJobState(String name, String message) {
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
  private final JobService jobService;

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
    final AddJobState currentState = (AddJobState) userState.getCommandState();
    final String userInput = userRequest.getText();
    final Long chatId = userRequest.getChatId();

    switch (currentState) {
      case WAITING_FOR_TITLE:
        userState.addInput("title", userInput);
        userState.setCommandState(AddJobState.WAITING_FOR_DESCRIPTION);
        telegramService.sendMessage(chatId, AddJobState.WAITING_FOR_DESCRIPTION.getMessage());
        userStateService.setStateForUser(chatId, userState);
        break;

      case WAITING_FOR_DESCRIPTION:
        userState.addInput("description", userInput);
        userState.setCommandState(AddJobState.WAITING_FOR_ACTIVITIES);
        telegramService.sendMessage(chatId, AddJobState.WAITING_FOR_ACTIVITIES.getMessage());
        userStateService.setStateForUser(chatId, userState);
        break;

      case WAITING_FOR_ACTIVITIES:
        userState.addInput("activities", userInput);
        userState.setCommandState(AddJobState.WAITING_FOR_COMPETENCES);
        telegramService.sendMessage(chatId, AddJobState.WAITING_FOR_COMPETENCES.getMessage());
        userStateService.setStateForUser(chatId, userState);
        break;

      case WAITING_FOR_COMPETENCES:
        userState.addInput("competences", userInput);
        final Map<String, String> inputs = userState.getInputs();
        final AddJobDTO addJobDTO =
            AddJobDTO.builder()
                .title(inputs.get("title"))
                .description(inputs.get("description"))
                .activities(List.of(inputs.get("activities")))
                .competences(List.of(inputs.get("competences")))
                .build();
        jobService.addJob(addJobDTO);
        telegramService.sendMessage(chatId, "Successfully added new job!");
        userState.clear();
        userStateService.setStateForUser(chatId, userState);
        break;
    }
  }

  private void initializeUserState(UserRequest userRequest, UserState userState) {
    final Long chatId = userRequest.getChatId();
    userState.setCurrentCommand(Command.ADD_JOB);
    userState.setCommandState(AddJobState.WAITING_FOR_TITLE);
    userStateService.setStateForUser(chatId, userState);
    telegramService.sendMessage(chatId, AddJobState.WAITING_FOR_TITLE.getMessage());
  }

  @Override
  public boolean canHandle(Command command) {
    return command.equals(Command.ADD_JOB);
  }
}
