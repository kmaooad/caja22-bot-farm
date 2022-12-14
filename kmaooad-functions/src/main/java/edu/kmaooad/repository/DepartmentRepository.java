package edu.kmaooad.repository;

import edu.kmaooad.domain.model.Department;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DepartmentRepository extends MongoRepository<Department, String> {}