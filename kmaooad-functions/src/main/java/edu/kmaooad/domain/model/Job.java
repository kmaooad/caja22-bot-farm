package edu.kmaooad.domain.model;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Value
@Builder
@Jacksonized
@Document("jobs")
public class Job {

    @Id
    String id;

    String title;
    String description;
    Boolean isActive;
    List<String> activities;
    List<String> competences;
}
