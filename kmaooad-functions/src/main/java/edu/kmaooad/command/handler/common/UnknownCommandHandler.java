package edu.kmaooad.command.handler.common;

import edu.kmaooad.command.dispatch.Command;
import edu.kmaooad.command.handler.CommandHandler;
import edu.kmaooad.service.TelegramService;
import edu.kmaooad.web.request.UserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UnknownCommandHandler implements CommandHandler {

  private final TelegramService telegramService;

  @Override
  public void handle(UserRequest userRequest) {
    telegramService.sendMessage(
        userRequest.getChatId(), "Unknown command! Please, provide a valid command");
  }

  @Override
  public boolean canHandle(Command command) {
    return command.equals(Command.UNKNOWN_COMMAND);
  }
}
