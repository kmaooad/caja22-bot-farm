package edu.kmaooad.domain.model;

import edu.kmaooad.command.Command;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import edu.kmaooad.command.handler.CommandState;
import lombok.*;

@Getter
@Setter
public class UserState {

  private final Long chatId;
  private Command currentCommand;
  private CommandState commandState;
  private final Map<String, String> inputs;

  public UserState(Long chatId, Command currentCommand, CommandState commandState, Map<String, String> inputs) {
    this.chatId = chatId;
    this.currentCommand = currentCommand;
    this.commandState = commandState;
    this.inputs = inputs;
  }

  public static UserState newEmptyState(Long chatId) {
    return new UserState(chatId, null, null, new HashMap<>());
  }

  public void clear() {
    this.inputs.clear();
    this.currentCommand = null;
    this.commandState = null;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof UserState)) {
      return false;
    } else {
      final UserState other = (UserState) o;
      return chatId.equals(other.getChatId());
    }
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(chatId);
  }
}
