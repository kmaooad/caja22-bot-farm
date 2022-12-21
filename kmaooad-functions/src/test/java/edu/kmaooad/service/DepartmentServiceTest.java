package edu.kmaooad.service;

import edu.kmaooad.domain.dto.department.UpdateDepartmentDTO;
import edu.kmaooad.domain.mapper.DepartmentMapper;
import edu.kmaooad.domain.model.Department;
import edu.kmaooad.repository.DepartmentRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

public class DepartmentServiceTest {

    private DepartmentRepository depRepository;
    private DepartmentMapper depMapper;
    private DepartmentService depService;

    @BeforeEach
    public void setup() {
        depRepository = mock(DepartmentRepository.class);
        depMapper = mock(DepartmentMapper.class);
        depService = new DepartmentServiceImpl(depRepository, depMapper);
    }

    @Test
    public void shouldGetAllDepartments() {
        Department expected = Department.builder().id("dep-id").build();
        when(depRepository.findAll()).thenReturn(List.of(expected));
        List<Department> departments = depService.getAllDepartments();
        Assertions.assertEquals(departments.size(), 1);
        Assertions.assertEquals(departments.get(0), expected);
        verify(depRepository, times(1)).findAll();
    }

    @Test
    public void shouldGetDepartmentById() {
        Department expected = Department.builder().id("dep-id").build();
        when(depRepository.findById(anyString())).thenReturn(Optional.of(expected));
        Optional<Department> dep = depService.getDepartmentById("dep-id");
        Assertions.assertTrue(dep.isPresent());
        Assertions.assertEquals(dep.get(), expected);
        verify(depRepository, times(1)).findById(eq("dep-id"));
    }

    @Test
    public void shouldUpdateDepartmentStatus(){
        Department expected = Department.builder().isHiring(false).build();
        Department depToUpdate = Department.builder().build();
        when(depMapper.toDepartment(any(UpdateDepartmentDTO.class))).thenReturn(depToUpdate);
        when(depRepository.save(any(Department.class))).thenReturn(expected);
        UpdateDepartmentDTO updateDepartmentDTO = UpdateDepartmentDTO.builder().build();
        Department actual = depService.updateDepStatus(updateDepartmentDTO);
        Assertions.assertEquals(expected, actual);
        verify(depMapper, times(1)).toDepartment(eq(updateDepartmentDTO));
        verify(depRepository, times(1)).save(eq(depToUpdate));
    }

}
