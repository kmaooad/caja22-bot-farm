package edu.kmaooad.service;

import edu.kmaooad.domain.dto.cv.AddCVDTO;
import edu.kmaooad.domain.dto.cv.UpdateCVDTO;
import edu.kmaooad.domain.mapper.CVMapper;
import edu.kmaooad.domain.model.CV;
import edu.kmaooad.repository.CVRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CVServiceImpl implements CVService {

    private final CVRepository cvRepository;
    private final CVMapper cvMapper;

    @Override
    public Optional<CV> getCVById(String cvId) {
        return cvRepository.findById(cvId);
    }

    @Override
    public CV addCV(AddCVDTO addCVDTO) {
        final CV cv = cvMapper.toCV(addCVDTO);
        cvRepository.save(cv);
        return cv;
    }

    @Override
    public CV updateCV(UpdateCVDTO updateCVDTO) {
        final CV cv = cvMapper.toCV(updateCVDTO);
        cvRepository.save(cv);
        return cv;
    }

    @Override
    public boolean deleteCV(String cvID) {
        cvRepository.deleteById(cvID);
        return cvRepository.existsById(cvID);
    }
}

