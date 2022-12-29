package edu.kmaooad.domain.mapper;

import edu.kmaooad.domain.dto.department.UpdateDepartmentDTO;
import edu.kmaooad.domain.model.Department;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DepartmentMapperTest {

  private DepartmentMapper depMapper;

  @BeforeEach
  public void setup() {
    depMapper = new DepartmentMapperImpl();
  }

  @Test
  public void shouldMapUpdateDepartmentDTOToDepartmentEntity() {
    UpdateDepartmentDTO updateDepartmentDTO =
        UpdateDepartmentDTO.builder()
            .id("dep-id")
            .name("name")
            .isHiring(true)
            .orgId("org-id")
            .build();
    Department dep = depMapper.toDepartment(updateDepartmentDTO);
    Assertions.assertEquals(dep.getName(), updateDepartmentDTO.getName());
    Assertions.assertEquals(dep.getIsHiring(), updateDepartmentDTO.getIsHiring());
    Assertions.assertEquals(dep.getOrgId(), updateDepartmentDTO.getOrgId());
  }
}
