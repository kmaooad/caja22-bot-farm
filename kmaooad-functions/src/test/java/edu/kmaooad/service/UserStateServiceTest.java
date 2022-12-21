package edu.kmaooad.service;

import edu.kmaooad.command.dispatch.Command;
import edu.kmaooad.domain.model.UserState;
import java.util.HashMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UserStateServiceTest {

  private UserStateService userStateService;

  @BeforeEach
  public void setup() {
    userStateService = new UserStateServiceImpl();
  }

  @Test
  public void shouldCreateEmptyStateIfUserStateIsNotPresent() {
    UserState userState = userStateService.getStateForUser(1L);

    Assertions.assertEquals(userState.getChatId(), 1);
    Assertions.assertNull(userState.getCurrentCommand());
    Assertions.assertNull(userState.getCommandState());
    Assertions.assertNotNull(userState.getInputs());
    Assertions.assertEquals(userState.getInputs().size(), 0);
  }

  @Test
  public void shouldAddStateForUser() {
    UserState userState = new UserState(1L, Command.UNKNOWN_COMMAND, null, new HashMap<>());

    userStateService.setStateForUser(1L, userState);

    Assertions.assertEquals(userStateService.getStateForUser(1L), userState);
  }

  @Test
  public void shouldReplaceExistingStateForUser() {
    UserState firstUserState = new UserState(1L, Command.UNKNOWN_COMMAND, null, new HashMap<>());
    userStateService.setStateForUser(1L, firstUserState);
    Assertions.assertEquals(userStateService.getStateForUser(1L), firstUserState);

    UserState secondUserState = new UserState(1L, Command.ADD_JOB, null, new HashMap<>());
    userStateService.setStateForUser(1L, secondUserState);

    Assertions.assertEquals(userStateService.getStateForUser(1L), secondUserState);
  }
}
