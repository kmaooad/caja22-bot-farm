package edu.kmaooad.command.handler.cv;

import edu.kmaooad.command.dispatch.Command;
import edu.kmaooad.command.handler.CommandHandler;
import edu.kmaooad.domain.model.CV;
import edu.kmaooad.service.CVService;
import edu.kmaooad.service.TelegramService;
import edu.kmaooad.web.request.UserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetAllCVCommandHandler implements CommandHandler {

  private final CVService cvService;
  private final TelegramService telegramService;

  @Override
  public void handle(UserRequest userRequest) {
    final Long chatId = userRequest.getChatId();
    StringBuilder stringBuilder = new StringBuilder("All available cvs:\n");
    for (CV cv : cvService.getAllCVs()) {
      stringBuilder.append(cv.toString()).append("\n");
    }
    telegramService.sendMessage(chatId, stringBuilder.toString());
  }

  @Override
  public boolean canHandle(Command command) {
    return command.equals(Command.GET_ALL_CV);
  }
}
