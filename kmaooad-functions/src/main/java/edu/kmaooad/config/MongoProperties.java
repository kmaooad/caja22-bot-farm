package edu.kmaooad.config;

import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@Value
@ConfigurationProperties(prefix = "mongo")
@ConstructorBinding
public class MongoProperties {

  String url;
  String database;
  String collection;
}
