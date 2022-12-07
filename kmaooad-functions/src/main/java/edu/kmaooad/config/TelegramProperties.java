package edu.kmaooad.config;

import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConfigurationProperties(prefix = "telegram")
@ConstructorBinding
@Value
public class TelegramProperties {

  String botName;
  String botToken;
}
