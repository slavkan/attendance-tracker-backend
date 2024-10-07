package com.fsre.attendance_tracker_backend.service;

import com.fsre.attendance_tracker_backend.model.FacultyPerson;
import com.fsre.attendance_tracker_backend.model.Person;
import com.fsre.attendance_tracker_backend.model.Subject;
import com.fsre.attendance_tracker_backend.model.SubjectPerson;
import com.fsre.attendance_tracker_backend.model.dto.ApiResponse;
import com.fsre.attendance_tracker_backend.repo.PersonRepo;
import com.fsre.attendance_tracker_backend.repo.SubjectPersonRepo;
import com.fsre.attendance_tracker_backend.repo.SubjectRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SubjectPersonService {

    @Autowired
    private SubjectPersonRepo subjectPersonRepo;

    @Autowired
    private PersonRepo personRepo;

    @Autowired
    private SubjectRepo subjectRepo;

    public SubjectPerson getSubjectPerson (Long id) {
        return subjectPersonRepo.findById(id).orElse(null);
    }

    public SubjectPerson assignPersonToSubject(Long personId, Long subjectId) {
        Person person = personRepo.findById(personId).orElseThrow(() -> new RuntimeException("Person not found"));
        Subject subject = subjectRepo.findById(subjectId).orElseThrow(() -> new RuntimeException("Subject not found"));
        if (subjectPersonRepo.findByPersonIdAndSubjectId(personId, subjectId).isPresent()) {
            throw new RuntimeException("Person and subject are already connected");
        }

        SubjectPerson subjectPerson = new SubjectPerson();
        subjectPerson.setPerson(person);
        subjectPerson.setSubject(subject);

        return subjectPersonRepo.save(subjectPerson);
    }

    public List<SubjectPerson> getAllSubjectsByPerson(Long personId) {
        return subjectPersonRepo.findByPersonId(personId);
    }

    public ApiResponse removePersonFromSubject(Long personId, Long subjectId) {
        Optional<SubjectPerson> existingSubjectPersonOptional = subjectPersonRepo.findByPersonIdAndSubjectId(personId, subjectId);
        if (existingSubjectPersonOptional.isPresent()) {
            try {
                subjectPersonRepo.delete(existingSubjectPersonOptional.get());
                return new ApiResponse("User removed from subject");
            } catch (DataIntegrityViolationException e) {
                throw new RuntimeException("Cannot remove user from subject due to foreign key constraints");
            }
        } else {
            throw new RuntimeException("User and subject connection not found");
        }
    }

}
