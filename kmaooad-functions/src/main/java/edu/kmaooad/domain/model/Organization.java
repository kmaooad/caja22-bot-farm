package edu.kmaooad.domain.model;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Value
@Builder
@Jacksonized
@Document("organizations")
public class Organization {
    @Id String id;
    String name;
}