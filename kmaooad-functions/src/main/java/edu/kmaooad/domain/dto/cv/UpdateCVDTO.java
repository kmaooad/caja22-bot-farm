package edu.kmaooad.domain.dto.cv;

import java.util.List;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.springframework.data.annotation.Id;

@Value
@Builder
@Jacksonized
public class UpdateCVDTO {

    String id;
    String name;
    String description;
    Boolean isActive;
    Boolean manageCompetencies;
    List<String> activities;
    List<String> preferences;
    List<String> competences;
}
