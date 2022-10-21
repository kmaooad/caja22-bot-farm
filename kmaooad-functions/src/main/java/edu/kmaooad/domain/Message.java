package edu.kmaooad.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("messages")
@Value
@Builder
@Jacksonized
public class Message {

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  @Id
  String id;

  @JsonProperty("update_id")
  Long updateId;

  Content message;

  @Value
  @Builder
  @Jacksonized
  public static class Content {
    @JsonProperty("message_id")
    Long messageId;

    User from;
    Chat chat;
    Long date;
    String text;
  }

  @Value
  @Builder
  @Jacksonized
  public static class User {
    Long id;

    @JsonProperty("is_bot")
    Boolean isBot;

    @JsonProperty("first_name")
    String firstName;

    @JsonProperty("last_name")
    String lastName;

    String username;

    @JsonProperty("language_code")
    String languageCode;
  }

  @Value
  @Builder
  @Jacksonized
  public static class Chat {
    Long id;

    @JsonProperty("first_name")
    String firstName;

    @JsonProperty("last_name")
    String lastName;

    String username;
    String type;
  }
}
