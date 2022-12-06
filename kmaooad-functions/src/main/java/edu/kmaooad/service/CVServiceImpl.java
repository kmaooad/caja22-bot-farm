package edu.kmaooad.service;

import edu.kmaooad.domain.dto.cv.AddCVDTO;
import edu.kmaooad.domain.dto.cv.UpdateCVDTO;
import edu.kmaooad.domain.mapper.CVMapper;
import edu.kmaooad.domain.model.CV;
import edu.kmaooad.domain.model.Job;
import edu.kmaooad.repository.CVRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.startsWith;

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
    public Optional<CV> getCVByName(String fullName) {
        Query query = new Query();
        query.addCriteria(Criteria.where("name").is(fullName));


        CV cvExample = new CV("0", fullName, "", false, false, new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>());
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("name", startsWith().ignoreCase());

        return cvRepository.findAll(Example.of(cvExample, matcher)).stream().findFirst();
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

    @Override
    public List<CV> getAllCVs() {
        return cvRepository.findAll();
    }
}

