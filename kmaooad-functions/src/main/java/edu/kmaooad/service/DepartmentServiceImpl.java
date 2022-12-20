package edu.kmaooad.service;

import edu.kmaooad.domain.dto.department.UpdateDepartmentDTO;
import edu.kmaooad.domain.mapper.DepartmentMapper;
import edu.kmaooad.domain.model.Department;
import edu.kmaooad.repository.DepartmentRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

  private final DepartmentRepository depRepository;
  private final DepartmentMapper depMapper;

  @Override
  public Optional<Department> getDepartmentById(String depId) {
    return depRepository.findById(depId);
  }

  @Override
  public Department updateDepStatus(UpdateDepartmentDTO updateDepartmentDTO) {
    final Department dep = depMapper.toDepartment(updateDepartmentDTO);
    return depRepository.save(dep);
  }

  @Override
  public List<Department> getAllDepartments() {
    return depRepository.findAll();
  }
}
