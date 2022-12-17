package edu.kmaooad.repository;

import edu.kmaooad.domain.model.Organization;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrganizationRepository extends MongoRepository<Organization, String> {}
