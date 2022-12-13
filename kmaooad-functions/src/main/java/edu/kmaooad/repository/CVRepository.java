package edu.kmaooad.repository;

import edu.kmaooad.domain.model.CV;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CVRepository extends MongoRepository<CV, String> {

  Optional<CV> findByName(String name);
}
