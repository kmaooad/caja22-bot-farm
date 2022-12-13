package edu.kmaooad.domain.dto.job;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Value
@Builder
@Jacksonized
public class UpdateJobDTO {
    String id;
    String title;
    String description;
    Boolean isActive;
    List<String> activities;
    List<String> competences;
    String depId;
}
