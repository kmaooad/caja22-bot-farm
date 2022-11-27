package edu.kmaooad.command;

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
      // if user already has command - switch to new command, clear old state
      // start handling command
      // TODO: maybe move new state initialization to command handlers
      userStateService
          .getStateForUser(userRequest.getChatId())
          .ifPresentOrElse(
              UserState::clear,
              () -> {
                final Long chatId = userRequest.getChatId();
                final UserState emptyState = UserState.newEmptyState(chatId);
                userStateService.setStateForUser(chatId, emptyState);
              });

      final Command command = Command.fromString(userRequest.getText());
      final CommandHandler commandHandler = getHandlerForCommand(command);
      commandHandler.handle(userRequest);
    } else {
      // find active command for user and continue handling it
      // if no active command - ignore
      userStateService
          .getStateForUser(userRequest.getChatId())
          .map(UserState::getCurrentCommand)
          .map(this::getHandlerForCommand)
          .ifPresent(handler -> handler.handle(userRequest));
    }
  }

  private CommandHandler getHandlerForCommand(Command command) {
    return handlers.stream()
        .filter(handler -> handler.canHandle(command))
        .findFirst()
        .orElse(unknownCommandHandler);
  }
}
