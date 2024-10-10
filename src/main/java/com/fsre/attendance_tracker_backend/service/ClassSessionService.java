package com.fsre.attendance_tracker_backend.service;

import com.fsre.attendance_tracker_backend.model.ClassSession;
import com.fsre.attendance_tracker_backend.model.Person;
import com.fsre.attendance_tracker_backend.model.Subject;
import com.fsre.attendance_tracker_backend.repo.ClassAttendanceRepo;
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
    private ClassAttendanceRepo classAttendanceRepo;

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
        classSession.setOffsetInMinutes(0L);

        return classSessionRepo.save(classSession);
    }

    public ClassSession endClassSession(Long classSessionId, boolean nullifyUnfinishedAttendances) {
        Optional<ClassSession> existingClassSessionOptional = classSessionRepo.findById(classSessionId);
        if (existingClassSessionOptional.isPresent()) {
            Long offset = existingClassSessionOptional.get().getOffsetInMinutes();
            ClassSession existingClassSession = existingClassSessionOptional.get();
            existingClassSession.setEndTime(LocalDateTime.now().plusMinutes(offset));
            existingClassSession.setState(ClassSessionState.ENDED);

            /* If true, everyone who hasn't departed by scaning code to exit, it will be set like he was
            one minute in session */
            if (nullifyUnfinishedAttendances) {
                classAttendanceRepo.findByClassSessionId(classSessionId).forEach(classAttendance -> {
                    if (classAttendance.getDepartureTime() == null) {
                        classAttendance.setDepartureTime(LocalDateTime.now());
                        classAttendance.setDepartureTime(classAttendance.getArrivalTime().plusMinutes(1));
                        classAttendanceRepo.save(classAttendance);
                    }
                });
            } else {
                classAttendanceRepo.findByClassSessionId(classSessionId).forEach(classAttendance -> {
                    if (classAttendance.getDepartureTime() == null) {
                        classAttendance.setDepartureTime(LocalDateTime.now().plusMinutes(offset));
                        classAttendanceRepo.save(classAttendance);
                    }
                });
            }

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
