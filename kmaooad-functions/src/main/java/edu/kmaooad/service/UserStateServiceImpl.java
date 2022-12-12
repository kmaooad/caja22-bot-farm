package edu.kmaooad.service;

import edu.kmaooad.domain.model.UserState;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class UserStateServiceImpl implements UserStateService {

  private final Map<Long, UserState> userStateMap;

  public UserStateServiceImpl() {
    userStateMap = new HashMap<>();
  }

  @Override
  public UserState getStateForUser(Long chatId) {
    if (!userStateMap.containsKey(chatId)) {
      userStateMap.put(chatId, UserState.newEmptyState(chatId));
    }
    return userStateMap.get(chatId);
  }

  @Override
  public void setStateForUser(Long chatId, UserState userState) {
    userStateMap.put(chatId, userState);
  }
}
