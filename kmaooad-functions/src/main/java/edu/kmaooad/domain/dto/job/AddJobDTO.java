package edu.kmaooad.domain.dto.job;

import java.util.List;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class AddJobDTO {

  String title;
  String description;
  List<String> activities;
  List<String> competences;
  String depId;
}
