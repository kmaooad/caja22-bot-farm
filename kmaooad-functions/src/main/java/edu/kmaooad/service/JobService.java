package edu.kmaooad.service;

import edu.kmaooad.domain.dto.job.AddJobDTO;
import edu.kmaooad.domain.model.Job;

public interface JobService {

    Job addJob(AddJobDTO addJobDTO);

    boolean deleteJob(String jobId);
}
