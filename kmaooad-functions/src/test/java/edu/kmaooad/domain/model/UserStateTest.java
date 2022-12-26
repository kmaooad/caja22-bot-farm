package edu.kmaooad.domain.model;

import edu.kmaooad.command.dispatch.Command;
import edu.kmaooad.command.handler.CommandState;
import java.util.HashMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UserStateTest {

  @Test
  public void shouldCreateEmptyUserState() {
    UserState userState = UserState.newEmptyState(1L);

    Assertions.assertEquals(userState.getChatId(), 1L);
    Assertions.assertNull(userState.getCurrentCommand());
    Assertions.assertNull(userState.getCommandState());
    Assertions.assertNotNull(userState.getInputs());
    Assertions.assertEquals(userState.getInputs().size(), 0);
  }

  @Test
  public void shouldReturnFalseForEmptyStateIfCommandAndCommandStateIsNotNull() {
    UserState userState =
        new UserState(1L, Command.UNKNOWN_COMMAND, new TestCommandState(), new HashMap<>());

    Assertions.assertFalse(userState.isEmpty());
  }

  @Test
  public void shouldReturnTrueForEmptyStateIfCommandIsNull() {
    UserState userState = new UserState(1L, null, new TestCommandState(), new HashMap<>());

    Assertions.assertTrue(userState.isEmpty());
  }

  @Test
  public void shouldReturnTrueForEmptyStateIfCommandStateIsNull() {
    UserState userState = new UserState(1L, Command.UNKNOWN_COMMAND, null, new HashMap<>());

    Assertions.assertTrue(userState.isEmpty());
  }

  @Test
  public void shouldReturnTrueForEmptyStateIfCommandAndCommandStateIsNull() {
    UserState userState = new UserState(1L, null, null, new HashMap<>());

    Assertions.assertTrue(userState.isEmpty());
  }

  @Test
  public void shouldAddInputToUserInputs() {
    UserState userState = UserState.newEmptyState(1L);
    Assertions.assertEquals(userState.getInputs().size(), 0);

    userState.addInput("input", "some-input");

    Assertions.assertEquals(userState.getInputs().size(), 1);
    Assertions.assertTrue(userState.getInputs().containsKey("input"));
    Assertions.assertEquals(userState.getInputs().get("input"), "some-input");
  }

  @Test
  public void shouldClearUserState() {
    UserState userState =
        new UserState(1L, Command.UNKNOWN_COMMAND, new TestCommandState(), new HashMap<>());
    Assertions.assertNotNull(userState.getCurrentCommand());
    Assertions.assertNotNull(userState.getCommandState());
    Assertions.assertEquals(userState.getInputs().size(), 0);

    userState.addInput("input", "some-input");
    Assertions.assertEquals(userState.getInputs().size(), 1);

    userState.clear();

    Assertions.assertEquals(userState.getInputs().size(), 0);
    Assertions.assertNull(userState.getCurrentCommand());
    Assertions.assertNull(userState.getCommandState());
  }

  @Test
  public void shouldBeEqualStatesIfIdsAreEqual() {
    UserState state1 = UserState.newEmptyState(1L);
    UserState state2 = UserState.newEmptyState(1L);

    Assertions.assertEquals(state1, state2);
  }

  @Test
  public void shouldNotBeEqualStatesIfComparedObjectsIsOfOtherClass() {
    UserState state1 = UserState.newEmptyState(1L);
    Object random = new Object();

    Assertions.assertNotEquals(state1, random);
  }

  private static class TestCommandState implements CommandState {

    @Override
    public String getName() {
      return "testState";
    }
  }
}
