package edu.kmaooad.command.dispatch;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import edu.kmaooad.command.handler.CommandHandler;
import edu.kmaooad.command.handler.CommandState;
import edu.kmaooad.domain.model.UserState;
import edu.kmaooad.service.UserStateService;
import edu.kmaooad.web.request.UserRequest;
import java.util.HashMap;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CommandDispatcherTest {

  private UserStateService userStateService;
  private CommandHandler commandHandler;
  private CommandHandler unknownCommandHandler;

  private CommandDispatcher commandDispatcher;

  @BeforeEach
  public void setup() {
    userStateService = mock(UserStateService.class);
    commandHandler = mock(CommandHandler.class);
    unknownCommandHandler = mock(CommandHandler.class);
    commandDispatcher =
        new CommandDispatcher(List.of(commandHandler), unknownCommandHandler, userStateService);
  }

  @Test
  public void shouldCreateCommandFromRequestStringAndSendToHandler() {
    UserRequest userRequest =
        UserRequest.builder().chatId(1L).text("/cancel").isCommand(true).build();
    when(commandHandler.canHandle(any(Command.class))).thenReturn(true);

    commandDispatcher.dispatch(userRequest);

    verify(commandHandler, times(1)).canHandle(Command.CANCEL);
    verify(commandHandler, times(1)).handle(eq(userRequest));
  }

  @Test
  public void shouldSendUnknownCommandToDefaultHandler() {
    UserRequest userRequest =
        UserRequest.builder().chatId(1L).text("/someUnknownCommand").isCommand(true).build();
    when(commandHandler.canHandle(any(Command.class))).thenReturn(false);

    commandDispatcher.dispatch(userRequest);

    verify(commandHandler, times(1)).canHandle(Command.UNKNOWN_COMMAND);
    verify(unknownCommandHandler, times(1)).handle(eq(userRequest));
  }

  @Test
  public void shouldGetCurrentCommandFromUserStateAndSendToHandler() {
    UserRequest userRequest =
        UserRequest.builder().chatId(1L).text("some-text").isCommand(false).build();
    UserState userState =
        new UserState(1L, Command.CANCEL, new TestCommandState(), new HashMap<>());
    when(userStateService.getStateForUser(anyLong())).thenReturn(userState);
    when(commandHandler.canHandle(any(Command.class))).thenReturn(true);

    commandDispatcher.dispatch(userRequest);

    verify(userStateService, times(1)).getStateForUser(eq(1L));
    verify(commandHandler, times(1)).canHandle(Command.CANCEL);
    verify(commandHandler, times(1)).handle(eq(userRequest));
  }

  @Test
  public void shouldIgnoreSimpleTextMessageIfUserStateIsEmpty() {
    UserRequest userRequest =
        UserRequest.builder().chatId(1L).text("some-text").isCommand(false).build();
    UserState userState = new UserState(1L, null, null, new HashMap<>());
    when(userStateService.getStateForUser(anyLong())).thenReturn(userState);

    commandDispatcher.dispatch(userRequest);

    verify(userStateService, times(1)).getStateForUser(eq(1L));
    verify(commandHandler, never()).canHandle(Command.CANCEL);
    verify(commandHandler, never()).handle(eq(userRequest));
    verify(unknownCommandHandler, never()).handle(eq(userRequest));
  }

  private static class TestCommandState implements CommandState {

    @Override
    public String getName() {
      return "test-state";
    }
  }
}
