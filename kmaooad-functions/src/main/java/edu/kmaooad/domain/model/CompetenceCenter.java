package edu.kmaooad.domain.model;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.ArrayList;
import java.util.List;

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
                case "iOS":
                    competencies.add("Swift");
                case "Andriod":
                    competencies.add("Kotlin");
            }
        }

        if (competencies.isEmpty()) competencies.add("Something important");
        return competencies;
    }
}

