package edu.kmaooad.service;

import edu.kmaooad.domain.model.UserState;

public interface UserStateService {

  UserState getStateForUser(Long chatId);

  void setStateForUser(Long chatId, UserState userState);
}
