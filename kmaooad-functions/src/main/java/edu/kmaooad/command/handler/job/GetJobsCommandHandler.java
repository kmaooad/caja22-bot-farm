package edu.kmaooad.command.handler.job;

import edu.kmaooad.command.dispatch.Command;
import edu.kmaooad.command.handler.CommandHandler;
import edu.kmaooad.domain.model.Job;
import edu.kmaooad.domain.model.UserRequest;
import edu.kmaooad.service.JobService;
import edu.kmaooad.service.TelegramService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetJobsCommandHandler implements CommandHandler {

  private final JobService jobService;
  private final TelegramService telegramService;

  @Override
  public void handle(UserRequest userRequest) {
    final Long chatId = userRequest.getChatId();
    StringBuilder stringBuilder = new StringBuilder("All available jobs:\n");
    for (Job job : jobService.getAllJobs()) {
      stringBuilder.append(job.toString()).append("\n");
    }
    telegramService.sendMessage(chatId, stringBuilder.toString());
  }

  @Override
  public boolean canHandle(Command command) {
    return command.equals(Command.GET_JOBS);
  }
}
