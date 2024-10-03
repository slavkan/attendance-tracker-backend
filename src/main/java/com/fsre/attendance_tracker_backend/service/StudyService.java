package com.fsre.attendance_tracker_backend.service;

import com.fsre.attendance_tracker_backend.model.Faculty;
import com.fsre.attendance_tracker_backend.model.Person;
import com.fsre.attendance_tracker_backend.model.Study;
import com.fsre.attendance_tracker_backend.model.dto.ApiResponse;
import com.fsre.attendance_tracker_backend.repo.FacultyRepo;
import com.fsre.attendance_tracker_backend.repo.StudyRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@Service
public class StudyService {

    @Autowired
    private StudyRepo studyRepo;

    @Autowired
    private FacultyRepo facultyRepo;


    public Study addStudy(Study study) {
        Long facultyId = study.getFaculty().getId();
        Faculty faculty = facultyRepo.findById(facultyId).orElseThrow(() -> new RuntimeException("Faculty not found"));

        return studyRepo.save(study);
    }

    public Study updateStudy(Long id, Study study) {
        Optional<Study> existingStudyOptional = studyRepo.findById(id);
        if (existingStudyOptional.isPresent()) {
            Study existingStudy = existingStudyOptional.get();

            existingStudy.setName(study.getName());
            existingStudy.setFaculty(study.getFaculty());

            return studyRepo.save(existingStudy);
        } else {
            throw new RuntimeException("Study not found with id " + id);
        }
    }

    public ApiResponse deleteStudy(Long id) {
        Optional<Study> existingStudyOptional = studyRepo.findById(id);
        if (existingStudyOptional.isPresent()) {
            try {
                studyRepo.deleteById(id);
                return new ApiResponse("Study deleted successfully");
            } catch (DataIntegrityViolationException e) {
                throw new RuntimeException("Cannot delete study due to foreign key constraints");
            }
        } else {
            throw new RuntimeException("Study not found with id " + id);
        }
    }

}
