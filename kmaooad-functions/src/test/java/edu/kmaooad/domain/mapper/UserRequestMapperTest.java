package edu.kmaooad.domain.mapper;

import edu.kmaooad.web.request.UserRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

public class UserRequestMapperTest {

  private UserRequestMapper userRequestMapper;

  @BeforeEach
  public void setup() {
    userRequestMapper = new UserRequestMapperImpl();
  }

  @Test
  public void shouldMapUpdateToUserRequest() {
    Long chatId = 1L;
    String text = "message-text";
    Chat chat = new Chat();
    chat.setId(chatId);
    Message message = new Message();
    message.setChat(chat);
    message.setText(text);
    Update update = new Update();
    update.setMessage(message);

    UserRequest userRequest = userRequestMapper.toUserRequest(update);

    Assertions.assertEquals(userRequest.getChatId(), chatId);
    Assertions.assertEquals(userRequest.getText(), text);
    Assertions.assertFalse(userRequest.isCommand());
  }

  @Test
  public void shouldMapNullMessageToNullPropertiesInUserRequest() {
    Update update = new Update();

    UserRequest userRequest = userRequestMapper.toUserRequest(update);

    Assertions.assertNull(userRequest.getChatId());
    Assertions.assertNull(userRequest.getText());
    Assertions.assertFalse(userRequest.isCommand());
  }
}
