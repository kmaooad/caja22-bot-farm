package edu.kmaooad.domain.mapper;

import edu.kmaooad.domain.dto.cv.AddCVDTO;
import edu.kmaooad.domain.dto.cv.UpdateCVDTO;
import edu.kmaooad.domain.model.CV;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CVMapperTest {
  private CVMapper cvMapper;

  @BeforeEach
  public void setup() {
    cvMapper = new CVMapperImpl();
  }

  @Test
  public void shouldMapAddCVDTOToCVEntity() {
    AddCVDTO addCVDTO =
        AddCVDTO.builder()
            .name("name")
            .description("description")
            .isHidden(false)
            .activities(List.of("activity"))
            .competences(List.of("competence"))
            .preferences(List.of("preference"))
            .manageCompetencies(true)
            .build();
    CV cv = cvMapper.toCV(addCVDTO);
    Assertions.assertEquals(cv.getName(), addCVDTO.getName());
    Assertions.assertEquals(cv.getDescription(), addCVDTO.getDescription());
    Assertions.assertFalse(addCVDTO.getIsHidden());
    Assertions.assertEquals(cv.getActivities(), addCVDTO.getActivities());
    Assertions.assertEquals(cv.getCompetences(), addCVDTO.getCompetences());
    Assertions.assertEquals(cv.getPreferences(), addCVDTO.getPreferences());
    Assertions.assertTrue(addCVDTO.getManageCompetencies());
  }

  @Test
  public void shouldMapUpdateCVDTOToCVEntity() {
    UpdateCVDTO updateCVDTO =
        UpdateCVDTO.builder()
            .id("cv-id")
            .name("name")
            .description("description")
            .isHidden(false)
            .activities(List.of("activity"))
            .competences(List.of("competence"))
            .preferences(List.of("preference"))
            .manageCompetencies(true)
            .build();
    CV cv = cvMapper.toCV(updateCVDTO);
    Assertions.assertEquals(cv.getName(), updateCVDTO.getName());
    Assertions.assertEquals(cv.getDescription(), updateCVDTO.getDescription());
    Assertions.assertEquals(cv.getIsHidden(), updateCVDTO.getIsHidden());
    Assertions.assertEquals(cv.getActivities(), updateCVDTO.getActivities());
    Assertions.assertEquals(cv.getCompetences(), updateCVDTO.getCompetences());
    Assertions.assertEquals(cv.getPreferences(), updateCVDTO.getPreferences());
    Assertions.assertEquals(cv.getManageCompetencies(), updateCVDTO.getManageCompetencies());
  }
}
