package edu.kmaooad.domain.mapper;

import edu.kmaooad.domain.dto.job.AddJobDTO;
import edu.kmaooad.domain.dto.job.UpdateJobDTO;
import edu.kmaooad.domain.model.Job;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class JobMapperTest {

  private JobMapper jobMapper;

  @BeforeEach
  public void setup() {
    jobMapper = new JobMapperImpl();
  }

  @Test
  public void shouldMapAddJobDTOToJobEntity() {
    AddJobDTO addJobDTO =
        AddJobDTO.builder()
            .title("title")
            .description("description")
            .competences(List.of("competence"))
            .activities(List.of("activity"))
            .depId("dep-id")
            .build();

    Job job = jobMapper.toJob(addJobDTO);

    Assertions.assertEquals(job.getTitle(), addJobDTO.getTitle());
    Assertions.assertEquals(job.getDescription(), addJobDTO.getDescription());
    Assertions.assertEquals(job.getCompetences(), addJobDTO.getCompetences());
    Assertions.assertEquals(job.getActivities(), addJobDTO.getActivities());
    Assertions.assertEquals(job.getDepId(), addJobDTO.getDepId());
    Assertions.assertTrue(job.getIsActive());
  }

  @Test
  public void shouldMapUpdateJobDTOToJobEntity() {
    UpdateJobDTO updateJobDTO =
        UpdateJobDTO.builder()
            .id("job-id")
            .title("title")
            .description("description")
            .competences(List.of("competence"))
            .activities(List.of("activity"))
            .isActive(false)
            .depId("dep-id")
            .build();

    Job job = jobMapper.toJob(updateJobDTO);

    Assertions.assertEquals(job.getId(), updateJobDTO.getId());
    Assertions.assertEquals(job.getTitle(), updateJobDTO.getTitle());
    Assertions.assertEquals(job.getDescription(), updateJobDTO.getDescription());
    Assertions.assertEquals(job.getCompetences(), updateJobDTO.getCompetences());
    Assertions.assertEquals(job.getActivities(), updateJobDTO.getActivities());
    Assertions.assertEquals(job.getIsActive(), updateJobDTO.getIsActive());
    Assertions.assertEquals(job.getDepId(), updateJobDTO.getDepId());
  }
}
