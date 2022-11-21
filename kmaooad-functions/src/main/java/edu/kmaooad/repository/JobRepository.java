package edu.kmaooad.repository;

import edu.kmaooad.domain.model.Job;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface JobRepository extends MongoRepository<Job, String> {}
