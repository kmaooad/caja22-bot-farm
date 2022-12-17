package edu.kmaooad.command.dispatch;

import java.util.stream.Stream;

public enum Command {
  GET_JOBS("/getAllJobs"),
  ADD_JOB("/addJob"),
  DELETE_JOB("/deleteJob"),
  CANCEL("/cancel"),
  ADD_CV("/addCV"),
  UPDATE_CV("/updateCV"),
  DELETE_CV("/deleteCV"),
  GET_ALL_CV("/getAllCV"),
  TOGGLE_CV_HIRES("/toggleOpenCV"),
  UPDATE_CV_COMPETENCES("/updateCVCompetences"),
  UPDATE_DEPARTMENT_STATUS("/updateDepartmentStatus"),
  UNKNOWN_COMMAND("");

  private final String value;

  Command(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public static Command fromString(String commandString) {
    return Stream.of(Command.values())
        .filter(command -> command.getValue().equals(commandString))
        .findFirst()
        .orElse(UNKNOWN_COMMAND);
  }
}
