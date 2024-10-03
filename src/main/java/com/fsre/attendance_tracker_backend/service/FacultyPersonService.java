package com.fsre.attendance_tracker_backend.service;

import com.fsre.attendance_tracker_backend.model.Faculty;
import com.fsre.attendance_tracker_backend.model.FacultyPerson;
import com.fsre.attendance_tracker_backend.model.Person;
import com.fsre.attendance_tracker_backend.model.Study;
import com.fsre.attendance_tracker_backend.model.dto.ApiResponse;
import com.fsre.attendance_tracker_backend.repo.FacultyPersonRepo;
import com.fsre.attendance_tracker_backend.repo.FacultyRepo;
import com.fsre.attendance_tracker_backend.repo.PersonRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FacultyPersonService {

    @Autowired
    private FacultyPersonRepo facultyPersonRepo;
    @Autowired
    private PersonRepo personRepo;
    @Autowired
    private FacultyRepo facultyRepo;


    public FacultyPerson getFacultyPerson (Long id) {
        return facultyPersonRepo.findById(id).orElse(null);
    }

    public FacultyPerson assignPersonToFaculty(Long personId, Long facultyId) {
        Person person = personRepo.findById(personId).orElseThrow(() -> new RuntimeException("Person not found"));
        Faculty faculty = facultyRepo.findById(facultyId).orElseThrow(() -> new RuntimeException("Faculty not found"));
        if (facultyPersonRepo.findByPersonIdAndFacultyId(personId, facultyId).isPresent()) {
            throw new RuntimeException("Person and faculty are already connected");
        }

        FacultyPerson facultyPerson = new FacultyPerson();
        facultyPerson.setPerson(person);
        facultyPerson.setFaculty(faculty);

        return facultyPersonRepo.save(facultyPerson);
    }


    public List<FacultyPerson> getAllFacultiesByPerson(Long personId) {
        return facultyPersonRepo.findByPersonId(personId);
    }

    public ApiResponse removePersonFromFaculty(Long id) {
        Optional<FacultyPerson> existingFacultyPersonOptional = facultyPersonRepo.findById(id);
        if (existingFacultyPersonOptional.isPresent()) {
            try {
                facultyPersonRepo.deleteById(id);
                return new ApiResponse("User removed from faculty");
            } catch (DataIntegrityViolationException e) {
                throw new RuntimeException("Cannot remove user from faculty due to foreign key constraints");
            }
        } else {
            throw new RuntimeException("User and faculty connection not found");
        }
    }

}
