package edu.kmaooad.domain.mapper;

import edu.kmaooad.domain.dto.cv.AddCVDTO;
import edu.kmaooad.domain.dto.cv.UpdateCVDTO;
import edu.kmaooad.domain.model.CV;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CVMapper {

    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "isActive", constant = "true")
    @Mapping(target = "activities", source = "activities")
    @Mapping(target = "competences", source = "competences")
    @Mapping(target = "preferences", source = "preferences")
    @Mapping(target = "manageCompetencies", constant = "false")
    CV toCV(AddCVDTO addCVDTO);
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "isActive", constant = "true")
    @Mapping(target = "activities", source = "activities")
    @Mapping(target = "competences", source = "competences")
    @Mapping(target = "preferences", source = "preferences")
    @Mapping(target = "manageCompetencies", constant = "false")
    CV toCV(UpdateCVDTO updateCVDTO);
}

