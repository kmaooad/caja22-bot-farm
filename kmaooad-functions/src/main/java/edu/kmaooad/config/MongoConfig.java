package edu.kmaooad.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class MongoConfig {

  private final MongoProperties mongoProperties;

  @Bean
  public MongoClientSettings mongoClientSettings() {
    final ConnectionString connectionString = new ConnectionString(mongoProperties.getUrl());
    return MongoClientSettings.builder()
        .applyConnectionString(connectionString)
        .serverApi(ServerApi.builder().version(ServerApiVersion.V1).build())
        .build();
  }
}
