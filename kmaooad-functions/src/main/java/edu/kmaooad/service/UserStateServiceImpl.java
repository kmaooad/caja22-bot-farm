package edu.kmaooad.service;

import edu.kmaooad.command.Command;
import edu.kmaooad.domain.model.UserState;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class UserStateServiceImpl implements UserStateService {

  private final Map<Long, UserState> userStateMap;

  public UserStateServiceImpl() {
    userStateMap = new HashMap<>();
  }

  @Override
  public Optional<UserState> getStateForUser(Long chatId) {
    return Optional.ofNullable(userStateMap.get(chatId));
  }

  @Override
  public void setStateForUser(Long chatId, UserState userState) {
    userStateMap.put(chatId, userState);
  }
}
