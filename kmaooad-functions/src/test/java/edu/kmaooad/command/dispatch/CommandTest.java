package edu.kmaooad.command.dispatch;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CommandTest {

  @Test
  public void shouldReturnCommandByItsName() {
    Command actual = Command.fromString("/cancel");

    Assertions.assertEquals(actual, Command.CANCEL);
  }

  @Test
  public void shouldReturnUnknownCommandIfValueIsWrong() {
    Command actual = Command.fromString("some-unknown-command");

    Assertions.assertEquals(actual, Command.UNKNOWN_COMMAND);
  }
}
