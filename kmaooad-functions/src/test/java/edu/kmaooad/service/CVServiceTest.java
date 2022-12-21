package edu.kmaooad.service;

import edu.kmaooad.domain.dto.cv.AddCVDTO;
import edu.kmaooad.domain.dto.cv.UpdateCVDTO;
import edu.kmaooad.domain.mapper.CVMapper;
import edu.kmaooad.domain.model.CV;
import edu.kmaooad.repository.CVRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

public class CVServiceTest {

    private CVRepository cvRepository;
    private CVMapper cvMapper;
    private CVService cvService;

    @BeforeEach
    public void setup() {
        cvRepository = mock(CVRepository.class);
        cvMapper = mock(CVMapper.class);
        cvService = new CVServiceImpl(cvRepository, cvMapper);
    }

    @Test
    public void shouldGetAllCVs() {
        CV expected = CV.builder().id("cv-id").build();
        when(cvRepository.findAll()).thenReturn(List.of(expected));
        List<CV> cvs = cvService.getAllCVs();
        Assertions.assertEquals(cvs.size(), 1);
        Assertions.assertEquals(cvs.get(0), expected);
        verify(cvRepository, times(1)).findAll();
    }

    @Test
    public void shouldGetCVById() {
        CV expected = CV.builder().id("cv-id").build();
        when(cvRepository.findById(anyString())).thenReturn(Optional.of(expected));
        Optional<CV> cv = cvService.getCVById("cv-id");
        Assertions.assertTrue(cv.isPresent());
        Assertions.assertEquals(cv.get(), expected);
        verify(cvRepository, times(1)).findById(eq("cv-id"));
    }

    @Test
    public void shouldGetCVByName() {
        CV expected = CV.builder().name("cv-name").build();
        when(cvRepository.findByName(anyString())).thenReturn(Optional.of(expected));
        Optional<CV> cv = cvService.getCVByName("cv-name");
        Assertions.assertTrue(cv.isPresent());
        Assertions.assertEquals(cv.get(), expected);
        verify(cvRepository, times(1)).findByName(eq("cv-name"));
    }

    @Test
    public void shouldAddNewCV() {
        CV expected = CV.builder()
                .id("cv-id")
                .name("name")
                .description("description")
                .isHidden(false)
                .activities(List.of("activity"))
                .competences(List.of("competence"))
                .preferences(List.of("preference"))
                .manageCompetencies(true)
                .build();
        CV cvToSave = CV.builder().build();
        when(cvMapper.toCV(any(AddCVDTO.class))).thenReturn(cvToSave);
        when(cvRepository.save(any(CV.class))).thenReturn(expected);
        AddCVDTO addCVDTO = AddCVDTO.builder().build();
        CV actual = cvService.addCV(addCVDTO);
        Assertions.assertEquals(expected, actual);
        verify(cvMapper, times(1)).toCV(eq(addCVDTO));
        verify(cvRepository, times(1)).save(eq(cvToSave));
    }

    @Test
    public void shouldUpdateCV(){
        CV expected = CV.builder().id("cv-id").build();
        CV cvToUpdate = CV.builder().build();
        when(cvMapper.toCV(any(UpdateCVDTO.class))).thenReturn(cvToUpdate);
        when(cvRepository.save(any(CV.class))).thenReturn(expected);
        UpdateCVDTO updateJobDTO = UpdateCVDTO.builder().build();
        CV actual = cvService.updateCV(updateJobDTO);
        Assertions.assertEquals(expected, actual);
        verify(cvMapper, times(1)).toCV(eq(updateJobDTO));
        verify(cvRepository, times(1)).save(eq(cvToUpdate));
    }

    @Test
    public void shouldDeleteCV() {
        boolean exists = false;
        when(cvRepository.existsById(anyString())).thenReturn(exists);
        boolean actual = cvService.deleteCV("cv-id");
        Assertions.assertEquals(actual, !exists);
        verify(cvRepository, times(1)).deleteById(eq("cv-id"));
        verify(cvRepository, times(1)).existsById(eq("cv-id"));
    }


}
