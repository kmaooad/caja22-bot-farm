package edu.kmaooad.domain.model;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Value
@Builder
@Jacksonized
@Document("departments")
public class Department {
  @Id String id;
  String name;
  Boolean isHiring;
  String orgId;
}
