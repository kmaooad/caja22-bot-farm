package edu.kmaooad.domain.mapper;

import edu.kmaooad.domain.dto.job.AddJobDTO;
import edu.kmaooad.domain.model.Job;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface JobMapper {

  @Mapping(target = "title", source = "title")
  @Mapping(target = "description", source = "description")
  @Mapping(target = "isActive", constant = "true")
  @Mapping(target = "activities", source = "activities")
  @Mapping(target = "competences", source = "competences")
  Job toJob(AddJobDTO addJobDTO);
}
