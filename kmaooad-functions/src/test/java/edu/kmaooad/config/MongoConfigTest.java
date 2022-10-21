package edu.kmaooad.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApiVersion;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MongoConfigTest {

  @Test
  public void shouldProvideCorrectMongoSettings() {
    final MongoProperties mongoProperties = new MongoProperties("mongodb://some_url", null, null);
    final ConnectionString connectionString = new ConnectionString(mongoProperties.getUrl());
    final MongoConfig mongoConfig = new MongoConfig(mongoProperties);

    final MongoClientSettings mongoClientSettings = mongoConfig.mongoClientSettings();

    Assertions.assertEquals(
        connectionString.getApplicationName(), mongoClientSettings.getApplicationName());
    Assertions.assertEquals(ServerApiVersion.V1, mongoClientSettings.getServerApi().getVersion());
  }
}
