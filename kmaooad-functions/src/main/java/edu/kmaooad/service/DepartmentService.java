package edu.kmaooad.service;

import edu.kmaooad.domain.dto.department.UpdateDepartmentDTO;
import edu.kmaooad.domain.model.Department;

import java.util.List;
import java.util.Optional;

public interface DepartmentService {

    Optional<Department> getDepartmentById(String depId);

    Department updateDepStatus(UpdateDepartmentDTO updateDepartmentDTO);

    List<Department> getAllDepartments();

}