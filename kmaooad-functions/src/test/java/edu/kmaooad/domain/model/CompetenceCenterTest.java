package edu.kmaooad.domain.model;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CompetenceCenterTest {

  private CompetenceCenter competenceCenter;

  @BeforeEach
  public void setup() {
    competenceCenter = new CompetenceCenter();
  }

  @Test
  public void shouldAddJavaIfBackendActivitySpecified() {
    List<String> competences = competenceCenter.generateCompetencies(List.of("Backend"));

    Assertions.assertEquals(competences.size(), 1);
    Assertions.assertEquals(competences.get(0), "Java");
  }

  @Test
  public void shouldAddSwiftIfIOSActivitySpecified() {
    List<String> competences = competenceCenter.generateCompetencies(List.of("iOS"));

    Assertions.assertEquals(competences.size(), 1);
    Assertions.assertEquals(competences.get(0), "Swift");
  }

  @Test
  public void shouldAddKotlinIfAndroidActivitySpecified() {
    List<String> competences = competenceCenter.generateCompetencies(List.of("Android"));

    Assertions.assertEquals(competences.size(), 1);
    Assertions.assertEquals(competences.get(0), "Kotlin");
  }

  @Test
  public void shouldAddSomethingImportantIfNoActivitySpecified() {
    List<String> competences = competenceCenter.generateCompetencies(List.of());

    Assertions.assertEquals(competences.size(), 1);
    Assertions.assertEquals(competences.get(0), "Something important");
  }
}
