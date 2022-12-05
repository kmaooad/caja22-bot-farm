package edu.kmaooad.domain.model;

import java.util.List;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Value
@Builder
@Jacksonized
@Document("cvs")
public class CV {

    @Id String id;

    String name;
    String description;
    Boolean isActive;
    Boolean manageCompetencies;
    List<String> activities;
    List<String> preferences;
    List<String> competences;
}

