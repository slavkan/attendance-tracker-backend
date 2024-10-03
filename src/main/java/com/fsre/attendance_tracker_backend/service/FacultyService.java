package com.fsre.attendance_tracker_backend.service;

import com.fsre.attendance_tracker_backend.model.Faculty;
import com.fsre.attendance_tracker_backend.model.Person;
import com.fsre.attendance_tracker_backend.model.Study;
import com.fsre.attendance_tracker_backend.model.dto.ApiResponse;
import com.fsre.attendance_tracker_backend.repo.FacultyRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FacultyService {

    @Autowired
    private FacultyRepo facultyRepo;

    public List<Faculty> getAllFaculties() {
        return facultyRepo.findAll();
    }

    public Faculty getFaculty (Long id) {
        return facultyRepo.findById(id).orElse(null);
    }

    public Faculty addFaculty(Faculty faculty) {
        return facultyRepo.save(faculty);
    }

    public Faculty updateFaculty(Long id, Faculty faculty) {
        Optional<Faculty> existingFacultyOptional = facultyRepo.findById(id);
        if (existingFacultyOptional.isPresent()) {
            Faculty existingFaculty = existingFacultyOptional.get();

            existingFaculty.setName(faculty.getName());
            existingFaculty.setAbbreviation(faculty.getAbbreviation());

            return facultyRepo.save(existingFaculty);
        } else {
            throw new RuntimeException("Faculty not found with id " + id);
        }
    }

    public ApiResponse deleteFaculty(Long id) {
        Optional<Faculty> existingFacultyOptional = facultyRepo.findById(id);
        if (existingFacultyOptional.isPresent()) {
            try {
                facultyRepo.deleteById(id);
                return new ApiResponse("Faculty deleted successfully");
            } catch (DataIntegrityViolationException e) {
                throw new RuntimeException("Cannot delete faculty due to foreign key constraints");
            }
        } else {
            throw new RuntimeException("Faculty not found with id " + id);
        }
    }


}
