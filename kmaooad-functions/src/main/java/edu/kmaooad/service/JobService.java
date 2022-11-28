package edu.kmaooad.service;

import edu.kmaooad.domain.dto.job.AddJobDTO;
import edu.kmaooad.domain.model.Job;
import java.util.List;

public interface JobService {

  List<Job> getAllJobs();

  Job addJob(AddJobDTO addJobDTO);

  boolean deleteJob(String jobId);
}
