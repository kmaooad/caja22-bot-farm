package edu.kmaooad.command;

import java.util.stream.Stream;

public enum Command {
  GET_JOBS("/getjobs"),
  ADD_JOB("/addjob"),
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
