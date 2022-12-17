package edu.kmaooad.config;

import lombok.Builder;
import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConfigurationProperties(prefix = "telegram")
@ConstructorBinding
@Value
@Builder
public class TelegramProperties {

  String botName;
  String botToken;
}
