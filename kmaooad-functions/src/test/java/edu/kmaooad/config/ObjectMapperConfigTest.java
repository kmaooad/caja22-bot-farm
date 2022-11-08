package edu.kmaooad.config;

import com.mongodb.assertions.Assertions;
import org.junit.jupiter.api.Test;

public class ObjectMapperConfigTest {

  @Test
  public void shouldReturnNonNullObjectMapper() {
    final ObjectMapperConfig objectMapperConfig = new ObjectMapperConfig();

    Assertions.assertNotNull(objectMapperConfig.objectMapper());
  }
}
