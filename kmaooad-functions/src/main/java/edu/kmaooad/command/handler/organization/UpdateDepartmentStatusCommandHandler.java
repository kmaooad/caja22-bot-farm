package edu.kmaooad.command.handler.organization;

import edu.kmaooad.command.dispatch.Command;
import edu.kmaooad.command.handler.CommandHandler;
import edu.kmaooad.command.handler.CommandState;
import edu.kmaooad.domain.dto.department.UpdateDepartmentDTO;
import edu.kmaooad.domain.dto.job.UpdateJobDTO;
import edu.kmaooad.domain.model.Department;
import edu.kmaooad.domain.model.Job;
import edu.kmaooad.domain.model.UserState;
import edu.kmaooad.service.DepartmentService;
import edu.kmaooad.service.JobService;
import edu.kmaooad.service.TelegramService;
import edu.kmaooad.service.UserStateService;
import edu.kmaooad.web.request.UserRequest;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UpdateDepartmentStatusCommandHandler implements CommandHandler {

  public enum UpdateDepartmentStatusState implements CommandState {
    WAITING_FOR_UPDATE_STATUS_DEPARTMENT_ID(
        "UPDATE_STATUS_DEPARTMENT_WAITING_FOR_ID",
        "Please, enter the department id of which you want to update hiring status"),
    WAITING_FOR_UPDATE_STATUS_DEPARTMENT_ACTION(
        "UPDATE_STATUS_DEPARTMENT_WAITING_FOR_ACTION",
        "Do you want to set the hiring status as 'We are hiring'(+) or 'We are not hiring'(-)?");

    private final String name;
    private final String message;

    UpdateDepartmentStatusState(String name, String message) {
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
  private final DepartmentService depService;
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
    final UpdateDepartmentStatusState currentState =
        (UpdateDepartmentStatusState) userState.getCommandState();
    final String userInput = userRequest.getText();
    final Long chatId = userRequest.getChatId();

    final Department dep;
    final UpdateDepartmentDTO updateDepartmentDTO;
    final Map<String, String> inputs;

    switch (currentState) {
      case WAITING_FOR_UPDATE_STATUS_DEPARTMENT_ID:
        userState.addInput("id", userInput);
        if (depService.getDepartmentById(userInput).isPresent()) {
          dep = depService.getDepartmentById(userInput).get();
          userState.setCommandState(
              UpdateDepartmentStatusState.WAITING_FOR_UPDATE_STATUS_DEPARTMENT_ACTION);
          telegramService.sendMessage(
              chatId,
              UpdateDepartmentStatusState.WAITING_FOR_UPDATE_STATUS_DEPARTMENT_ACTION.getMessage()
                  + " Department: "
                  + dep.getName());
          userStateService.setStateForUser(chatId, userState);
        } else {
          telegramService.sendMessage(
              chatId, "Department with provided id doesn't exists! Please, provide a valid id");
        }
        break;
      case WAITING_FOR_UPDATE_STATUS_DEPARTMENT_ACTION:
        userState.addInput("isHiring", userInput);
        inputs = userState.getInputs();
        dep = depService.getDepartmentById(inputs.get("id")).get();
        if (Objects.equals(userInput, "+")) {
          updateDepartmentDTO =
              UpdateDepartmentDTO.builder()
                  .id(dep.getId())
                  .name(dep.getName())
                  .isHiring(true)
                  .orgId(dep.getOrgId())
                  .build();
          depService.updateDepStatus(updateDepartmentDTO);
          userState.clear();
          userStateService.setStateForUser(chatId, userState);
          telegramService.sendMessage(
              chatId,
              "Successfully updated the status of the department! Now the department is open for"
                  + " hire!");
        } else if (Objects.equals(userInput, "-")) {
          // Stop hiring
          updateDepartmentDTO =
              UpdateDepartmentDTO.builder()
                  .id(dep.getId())
                  .name(dep.getName())
                  .isHiring(false)
                  .orgId(dep.getOrgId())
                  .build();
          depService.updateDepStatus(updateDepartmentDTO);
          // Deactivate all jobs
          List<Job> jobs =
              jobService.getAllJobs().stream()
                  .filter(job -> job.getDepId().equals(dep.getId()))
                  .collect(Collectors.toList());
          for (Job job : jobs) {
            final UpdateJobDTO updateJobDTO =
                UpdateJobDTO.builder()
                    .id(job.getId())
                    .title(job.getTitle())
                    .description(job.getDescription())
                    .isActive(false)
                    .activities(job.getActivities())
                    .competences(job.getCompetences())
                    .depId(job.getDepId())
                    .build();
            jobService.updateJob(updateJobDTO);
          }
          userState.clear();
          userStateService.setStateForUser(chatId, userState);
          telegramService.sendMessage(
              chatId,
              "Successfully updated the status of the department! Now the department is closed for"
                  + " hiring!");
        } else {
          telegramService.sendMessage(
              chatId,
              "Value for hiring status is not valid! Please, provide a valid value (+ or -) for the"
                  + " hiring status");
        }
        break;
    }
  }

  private void initializeUserState(UserRequest userRequest, UserState userState) {
    final Long chatId = userRequest.getChatId();
    userState.setCurrentCommand(Command.UPDATE_DEPARTMENT_STATUS);
    userState.setCommandState(UpdateDepartmentStatusState.WAITING_FOR_UPDATE_STATUS_DEPARTMENT_ID);
    userStateService.setStateForUser(chatId, userState);
    telegramService.sendMessage(
        chatId,
        UpdateDepartmentStatusCommandHandler.UpdateDepartmentStatusState
            .WAITING_FOR_UPDATE_STATUS_DEPARTMENT_ID
            .getMessage());
  }

  @Override
  public boolean canHandle(Command command) {
    return command.equals(Command.UPDATE_DEPARTMENT_STATUS);
  }
}
