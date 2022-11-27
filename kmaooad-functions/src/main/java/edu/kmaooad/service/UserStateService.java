package edu.kmaooad.service;

import edu.kmaooad.domain.model.UserState;
import java.util.Optional;

public interface UserStateService {

  Optional<UserState> getStateForUser(Long chatId);

  void setStateForUser(Long chatId, UserState userState);
}
