package edu.kmaooad.domain.mapper;

import edu.kmaooad.web.request.UserRequest;
import java.util.Objects;
import java.util.Optional;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Mapper(
    componentModel = "spring",
    imports = {Objects.class, Optional.class, Message.class})
public interface UserRequestMapper {

  @Mapping(
      target = "chatId",
      expression =
          "java(Optional.ofNullable(update.getMessage()).map(Message::getChatId).orElse(null))")
  @Mapping(
      target = "text",
      expression =
          "java(Optional.ofNullable(update.getMessage()).map(Message::getText).orElse(null))")
  @Mapping(
      target = "isCommand",
      expression = "java(Objects.nonNull(update.getMessage()) && update.getMessage().isCommand())")
  UserRequest toUserRequest(Update update);
}
