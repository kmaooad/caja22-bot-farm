package edu.kmaooad.service;

import edu.kmaooad.domain.dto.cv.AddCVDTO;
import edu.kmaooad.domain.dto.cv.UpdateCVDTO;
import edu.kmaooad.domain.model.CV;
import edu.kmaooad.domain.model.Job;

import java.util.List;
import java.util.Optional;

public interface CVService {

    Optional<CV> getCVById(String cvId);

    Optional<CV> getCVByName(String fullName);

    CV addCV(AddCVDTO addCVDTO);

    CV updateCV(UpdateCVDTO updateCVDTO);

    boolean deleteCV(String jobId);

    List<CV> getAllCVs();
}

