package edu.kmaooad.repository;

import edu.kmaooad.domain.model.CV;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CVRepository extends MongoRepository<CV, String> {}

