package com.fsre.attendance_tracker_backend.service;

import com.fsre.attendance_tracker_backend.model.Study;
import com.fsre.attendance_tracker_backend.model.Subject;
import com.fsre.attendance_tracker_backend.model.dto.ApiResponse;
import com.fsre.attendance_tracker_backend.repo.StudyRepo;
import com.fsre.attendance_tracker_backend.repo.SubjectRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class SubjectService {

    @Autowired
    private SubjectRepo subjectRepo;

    @Autowired
    private StudyRepo studyRepo;


    public List<Subject> getAllSubjectsByStudy(Long studyId) {
        return Optional.ofNullable(subjectRepo.findByStudyId(studyId)).orElse(Collections.emptyList());
    }

    public Subject addSubject(Subject subject) {
        Long studyId = subject.getStudy().getId();
        Study study = studyRepo.findById(studyId).orElseThrow(() -> new RuntimeException("Study not found"));

        return subjectRepo.save(subject);
    }

    public Subject updateSubject(Long id, Subject subject) {
        Optional<Subject> existingSubjectOptional = subjectRepo.findById(id);
        if(existingSubjectOptional.isPresent()) {
            Subject existingSubject = existingSubjectOptional.get();

            existingSubject.setName(subject.getName());
            existingSubject.setSemester(subject.getSemester());
            existingSubject.setStudy(subject.getStudy());

            return subjectRepo.save(existingSubject);
        } else {
            throw new RuntimeException("Study not found with id " + id);
        }
    }

    public ApiResponse deleteSubject(Long id) {
        Optional<Subject> existingSubjectOptional = subjectRepo.findById(id);
        if (existingSubjectOptional.isPresent()) {
            try {
                subjectRepo.deleteById(id);
                return new ApiResponse("Subject deleted successfully");
            } catch (DataIntegrityViolationException e) {
                throw new RuntimeException("Cannot delete subject due to foreign key constraints");
            }
        } else {
            throw new RuntimeException("Subject not found with id " + id);
        }
    }

}
