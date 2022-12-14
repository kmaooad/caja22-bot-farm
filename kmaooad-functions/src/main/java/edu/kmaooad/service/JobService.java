package edu.kmaooad.service;

import edu.kmaooad.domain.dto.job.AddJobDTO;
import edu.kmaooad.domain.dto.job.UpdateJobDTO;
import edu.kmaooad.domain.model.Job;
import java.util.List;
import java.util.Optional;

public interface JobService {

  List<Job> getAllJobs();

  Optional<Job> getJobById(String jobId);

  Job addJob(AddJobDTO addJobDTO);

  Job updateJob(UpdateJobDTO updateJobDTO);

  boolean deleteJob(String jobId);
}
