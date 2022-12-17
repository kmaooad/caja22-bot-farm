package edu.kmaooad.service;

import static org.mockito.Mockito.*;

import edu.kmaooad.domain.dto.job.AddJobDTO;
import edu.kmaooad.domain.dto.job.UpdateJobDTO;
import edu.kmaooad.domain.mapper.JobMapper;
import edu.kmaooad.domain.model.Job;
import edu.kmaooad.repository.JobRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class JobServiceTest {

  private JobRepository jobRepository;
  private JobMapper jobMapper;
  private JobService jobService;

  @BeforeEach
  public void setup() {
    jobRepository = mock(JobRepository.class);
    jobMapper = mock(JobMapper.class);
    jobService = new JobServiceImpl(jobRepository, jobMapper);
  }

  @Test
  public void shouldGetAllJobs() {
    Job expected = Job.builder().id("job-id").build();
    when(jobRepository.findAll()).thenReturn(List.of(expected));

    List<Job> jobs = jobService.getAllJobs();

    Assertions.assertEquals(jobs.size(), 1);
    Assertions.assertEquals(jobs.get(0), expected);

    verify(jobRepository, times(1)).findAll();
  }

  @Test
  public void shouldGetJobById() {
    Job expected = Job.builder().id("job-id").build();
    when(jobRepository.findById(anyString())).thenReturn(Optional.of(expected));

    Optional<Job> job = jobService.getJobById("job-id");

    Assertions.assertTrue(job.isPresent());
    Assertions.assertEquals(job.get(), expected);

    verify(jobRepository, times(1)).findById(eq("job-id"));
  }

  @Test
  public void shouldAddNewJob() {
    Job expected = Job.builder().id("job-id").build();
    Job jobToSave = Job.builder().build();
    when(jobMapper.toJob(any(AddJobDTO.class))).thenReturn(jobToSave);
    when(jobRepository.save(any(Job.class))).thenReturn(expected);

    AddJobDTO addJobDTO = AddJobDTO.builder().build();
    Job actual = jobService.addJob(addJobDTO);

    Assertions.assertEquals(expected, actual);

    verify(jobMapper, times(1)).toJob(eq(addJobDTO));
    verify(jobRepository, times(1)).save(eq(jobToSave));
  }

  @Test
  public void shouldUpdateJob() {
    Job expected = Job.builder().id("job-id").build();
    Job jobToUpdate = Job.builder().build();
    when(jobMapper.toJob(any(UpdateJobDTO.class))).thenReturn(jobToUpdate);
    when(jobRepository.save(any(Job.class))).thenReturn(expected);

    UpdateJobDTO updateJobDTO = UpdateJobDTO.builder().build();
    Job actual = jobService.updateJob(updateJobDTO);

    Assertions.assertEquals(expected, actual);

    verify(jobMapper, times(1)).toJob(eq(updateJobDTO));
    verify(jobRepository, times(1)).save(eq(jobToUpdate));
  }

  @Test
  public void shouldDeleteJob() {
    boolean exists = false;
    when(jobRepository.existsById(anyString())).thenReturn(exists);

    boolean actual = jobService.deleteJob("job-id");

    Assertions.assertEquals(actual, !exists);

    verify(jobRepository, times(1)).deleteById(eq("job-id"));
    verify(jobRepository, times(1)).existsById(eq("job-id"));
  }
}
