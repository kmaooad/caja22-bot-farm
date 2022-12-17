package edu.kmaooad.command.handler.common;

import edu.kmaooad.command.dispatch.Command;
import edu.kmaooad.command.handler.CommandHandler;
import edu.kmaooad.domain.model.UserState;
import edu.kmaooad.service.TelegramService;
import edu.kmaooad.service.UserStateService;
import edu.kmaooad.web.request.UserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CancelCommandHandler implements CommandHandler {

  private final TelegramService telegramService;
  private final UserStateService userStateService;

  @Override
  public void handle(UserRequest userRequest) {
    final Long chatId = userRequest.getChatId();
    final UserState userState = userStateService.getStateForUser(chatId);
    if (!userState.isEmpty()) {
      userState.clear();
    }
    telegramService.sendMessage(
        chatId,
        "Current active command was cancelled! Please, enter a command you want to execute");
  }

  @Override
  public boolean canHandle(Command command) {
    return command.equals(Command.CANCEL);
  }
}
