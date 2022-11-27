package edu.kmaooad.command.handler;

import edu.kmaooad.command.Command;
import edu.kmaooad.domain.model.UserRequest;
import edu.kmaooad.service.TelegramService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AddJobCommandHandler implements CommandHandler {

  private enum AddJobState implements CommandState {
    START("ADD_JOB_START"),
    WAITING_FOR_TITLE("ADD_JOB_WAITING_FOR_TITLE"),
    WAITING_FOR_DESCRIPTION("ADD_JOB_WAITING_FOR_DESCRIPTION"),
    WAITING_FOR_ACTIVITIES("ADD_JOB_WAITING_FOR_ACTIVITIES"),
    WAITING_FOR_COMPETENCES("ADD_JOB_WAITING_FOR_COMPETENCES"),
    FINISH("ADD_JOB_FINISH");

    private final String name;

    AddJobState(String name) {
      this.name = name;
    }

    @Override
    public String getName() {
      return name;
    }
  }

  private final TelegramService telegramService;

  @Override
  public void handle(UserRequest userRequest) {
    telegramService.sendMessage(userRequest.getChatId(), "Starting to add job!");

  }

  @Override
  public boolean canHandle(Command command) {
    return command.equals(Command.ADD_JOB);
  }
}
