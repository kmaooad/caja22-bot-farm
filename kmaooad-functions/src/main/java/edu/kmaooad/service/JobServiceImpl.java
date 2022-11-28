package edu.kmaooad.service;

import edu.kmaooad.domain.dto.job.AddJobDTO;
import edu.kmaooad.domain.mapper.JobMapper;
import edu.kmaooad.domain.model.Job;
import edu.kmaooad.repository.JobRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {

  private final JobRepository jobRepository;
  private final JobMapper jobMapper;

  @Override
  public List<Job> getAllJobs() {
    return jobRepository.findAll();
  }

  @Override
  public Job addJob(AddJobDTO addJobDTO) {
    final Job job = jobMapper.toJob(addJobDTO);
    jobRepository.save(job);
    return job;
  }

  @Override
  public boolean deleteJob(String jobId) {
    jobRepository.deleteById(jobId);
    return jobRepository.existsById(jobId);
  }
}
