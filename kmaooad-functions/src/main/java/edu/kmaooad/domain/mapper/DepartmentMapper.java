package edu.kmaooad.domain.mapper;

import edu.kmaooad.domain.dto.department.UpdateDepartmentDTO;
import edu.kmaooad.domain.model.Department;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DepartmentMapper {
  @Mapping(target = "id", source = "id")
  @Mapping(target = "name", source = "name")
  @Mapping(target = "isHiring", source = "isHiring")
  @Mapping(target = "orgId", source = "orgId")
  Department toDepartment(UpdateDepartmentDTO updateDepartmentDTO);
}
