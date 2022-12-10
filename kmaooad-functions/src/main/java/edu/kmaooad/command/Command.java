package edu.kmaooad.command;

import java.util.stream.Stream;

public enum Command {
  GET_JOBS("/getjobs"),
  ADD_JOB("/addjob"),
  DELETE_JOB("/deletejob"),
  CANCEL("/cancel"),
  ADD_CV("/addcv"),
  UPDATE_CV("/updatecv"),
  DELETE_CV("/deletecv"),
  GET_ALL_CV("/getallcv"),
  TOGGLE_CV_HIRES("/toggleOpenCV"),
  UPDATE_CV_COMPETENCES("/updateCVcompetences"),
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
