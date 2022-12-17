package edu.kmaooad.command.handler.job;

import edu.kmaooad.command.dispatch.Command;
import edu.kmaooad.command.handler.CommandHandler;
import edu.kmaooad.command.handler.CommandState;
import edu.kmaooad.domain.model.UserState;
import edu.kmaooad.service.JobService;
import edu.kmaooad.service.TelegramService;
import edu.kmaooad.service.UserStateService;
import edu.kmaooad.web.request.UserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeleteJobCommandHandler implements CommandHandler {

  private enum DeleteJobState implements CommandState {
    WAITING_FOR_JOB_ID(
        "DELETE_JOB_WAITING_FOR_ID", "Please, enter an id of job which you want to delete");

    private final String name;
    private final String message;

    DeleteJobState(String name, String message) {
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
    final DeleteJobState currentState = (DeleteJobState) userState.getCommandState();
    final String userInput = userRequest.getText();
    final Long chatId = userRequest.getChatId();

    if (currentState.equals(DeleteJobState.WAITING_FOR_JOB_ID)) {
      if (jobService.getJobById(userInput).isPresent()) {
        jobService.deleteJob(userInput);
        userState.clear();
        userStateService.setStateForUser(chatId, userState);
        telegramService.sendMessage(chatId, "Successfully deleted job!");
      } else {
        telegramService.sendMessage(
            chatId, "Job with provided id doesn't exists! Please, provide a valid id");
      }
    }
  }

  private void initializeUserState(UserRequest userRequest, UserState userState) {
    final Long chatId = userRequest.getChatId();
    userState.setCurrentCommand(Command.DELETE_JOB);
    userState.setCommandState(DeleteJobState.WAITING_FOR_JOB_ID);
    userStateService.setStateForUser(chatId, userState);
    telegramService.sendMessage(chatId, DeleteJobState.WAITING_FOR_JOB_ID.getMessage());
  }

  @Override
  public boolean canHandle(Command command) {
    return command.equals(Command.DELETE_JOB);
  }
}
