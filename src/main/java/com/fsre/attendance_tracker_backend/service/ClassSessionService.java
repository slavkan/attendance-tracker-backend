package com.fsre.attendance_tracker_backend.service;

import com.fsre.attendance_tracker_backend.model.ClassSession;
import com.fsre.attendance_tracker_backend.model.Person;
import com.fsre.attendance_tracker_backend.model.Subject;
import com.fsre.attendance_tracker_backend.repo.ClassSessionRepo;
import com.fsre.attendance_tracker_backend.repo.PersonRepo;
import com.fsre.attendance_tracker_backend.repo.SubjectRepo;
import com.fsre.attendance_tracker_backend.model.ClassSessionState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class ClassSessionService {

    @Autowired
    private ClassSessionRepo classSessionRepo;

    @Autowired
    private SubjectRepo subjectRepo;

    @Autowired
    private PersonRepo personRepo;


    public List<ClassSession> getAllClassSessionsBySubject(Long subjectId) {
        return Optional.ofNullable(classSessionRepo.findBySubjectId(subjectId)).orElse(Collections.emptyList());
    }

    public Optional<ClassSession> getClassSessionById(Long id) {
        return classSessionRepo.findById(id);
    }

    public ClassSession startNewClassSession(Long subjectId, Long professor_id) {
        Subject subject = subjectRepo.findById(subjectId).orElseThrow(() -> new RuntimeException("Subject not found"));
        Person professor = personRepo.findById(professor_id).orElseThrow(() -> new RuntimeException("Professor not found"));
        if (!professor.isProfessor()) {
            throw new RuntimeException("Person is not a professor");
        }

        ClassSession classSession = new ClassSession();
        classSession.setSubject(subject);
        classSession.setPerson(professor);
        classSession.setStartTime(LocalDateTime.now());
        classSession.setState(ClassSessionState.IN_PROGRESS);

        return classSessionRepo.save(classSession);
    }

    public ClassSession endClassSession(Long classSessionId) {
        Optional<ClassSession> existingClassSessionOptional = classSessionRepo.findById(classSessionId);
        if (existingClassSessionOptional.isPresent()) {
            ClassSession existingClassSession = existingClassSessionOptional.get();
            existingClassSession.setEndTime(LocalDateTime.now());
            existingClassSession.setState(ClassSessionState.ENDED);

            return classSessionRepo.save(existingClassSession);
        } else {
            throw new RuntimeException("Class session not found with id " + classSessionId);
        }
    }

    public ClassSession changeQRCode(Long classSessionId, String qrCode) {
        Optional<ClassSession> existingClassSessionOptional = classSessionRepo.findById(classSessionId);
        if (existingClassSessionOptional.isPresent()) {
            ClassSession existingClassSession = existingClassSessionOptional.get();
            existingClassSession.setCodeForArrivalPrevious(existingClassSession.getCodeForArrival());
            existingClassSession.setCodeForArrival(qrCode);
            return classSessionRepo.save(existingClassSession);
        } else {
            throw new RuntimeException("Class session not found with id " + classSessionId);
        }
    }

    public ClassSession setOffset(Long classSessionId, Long offset) {
        Optional<ClassSession> existingClassSessionOptional = classSessionRepo.findById(classSessionId);
        if (existingClassSessionOptional.isPresent()) {
            ClassSession existingClassSession = existingClassSessionOptional.get();
            existingClassSession.setOffsetInMinutes(offset);
            return classSessionRepo.save(existingClassSession);
        } else {
            throw new RuntimeException("Class session not found with id " + classSessionId);
        }
    }


}
