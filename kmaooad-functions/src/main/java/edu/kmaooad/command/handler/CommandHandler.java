package edu.kmaooad.command.handler;

import edu.kmaooad.command.dispatch.Command;
import edu.kmaooad.domain.model.UserRequest;

public interface CommandHandler {

  void handle(UserRequest userRequest);

  boolean canHandle(Command command);
}
