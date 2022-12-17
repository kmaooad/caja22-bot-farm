package edu.kmaooad.domain.model;

import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class CompetenceCenter {
  public CompetenceCenter() {}

  public List<String> generateCompetencies(List<String> activities) {
    List<String> competencies = new ArrayList<>();
    for (String activity : activities) {
      switch (activity) {
        case "Backend":
          competencies.add("Java");
          break;
        case "iOS":
          competencies.add("Swift");
          break;
        case "Android":
          competencies.add("Kotlin");
          break;
      }
    }

    if (competencies.isEmpty()) competencies.add("Something important");
    return competencies;
  }
}
