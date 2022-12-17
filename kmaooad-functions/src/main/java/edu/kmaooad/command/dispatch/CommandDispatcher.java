package edu.kmaooad.command.dispatch;

import edu.kmaooad.command.handler.CommandHandler;
import edu.kmaooad.domain.model.UserRequest;
import edu.kmaooad.domain.model.UserState;
import edu.kmaooad.service.UserStateService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommandDispatcher {

  private final List<CommandHandler> handlers;
  private final CommandHandler unknownCommandHandler;
  private final UserStateService userStateService;

  public void dispatch(UserRequest userRequest) {
    if (userRequest.isCommand()) {
      final Command command = Command.fromString(userRequest.getText());
      final CommandHandler commandHandler = getHandlerForCommand(command);
      commandHandler.handle(userRequest);
    } else {
      final UserState userState = userStateService.getStateForUser(userRequest.getChatId());
      if (!userState.isEmpty()) {
        final Command currentCommand = userState.getCurrentCommand();
        final CommandHandler commandHandler = getHandlerForCommand(currentCommand);
        commandHandler.handle(userRequest);
      }
    }
  }

  private CommandHandler getHandlerForCommand(Command command) {
    return handlers.stream()
        .filter(handler -> handler.canHandle(command))
        .findFirst()
        .orElse(unknownCommandHandler);
  }
}
