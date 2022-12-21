package edu.kmaooad.web.request;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class UserRequest {

  Long chatId;
  String text;
  boolean isCommand;
}
