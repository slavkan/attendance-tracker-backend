package com.fsre.attendance_tracker_backend.service;

import com.fsre.attendance_tracker_backend.model.*;
import com.fsre.attendance_tracker_backend.model.dto.StudentArrivalDto;
import com.fsre.attendance_tracker_backend.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Service
public class ClassAttendanceService {

    @Autowired
    private ClassAttendanceRepo classAttendanceRepo;

    @Autowired
    private PersonRepo personRepo;

    @Autowired
    private ClassSessionRepo classSessionRepo;

    @Autowired
    private SubjectPersonRepo subjectPersonRepo;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    public ClassAttendance studentToAttendance(Long classSessionId, Long personId, String code) {
        Person person = personRepo.findById(personId).orElseThrow(() -> new RuntimeException("Person not found"));
        ClassSession classSession = classSessionRepo.findById(classSessionId).orElseThrow(() -> new RuntimeException("Class Session not found"));

        Long subjectId = classSession.getSubject().getId();

        if (subjectPersonRepo.findByPersonIdAndSubjectId(personId, subjectId).isEmpty()) {
            throw new RuntimeException("Student is not part of this subject");
        }
        if(classSession.getState() != ClassSessionState.IN_PROGRESS) {
            throw new RuntimeException("Class session is not in progress");
        }
        if(!Objects.equals(classSession.getCodeForArrival(), code) && !Objects.equals(classSession.getCodeForArrivalPrevious(), code)) {
            throw new RuntimeException("Invalid code");
        }

        /* Check if code is current one, if not then student has sent previous code (which also works) */
        boolean isCodeCurrent = Objects.equals(classSession.getCodeForArrival(), code);



        Optional<ClassAttendance> classAttendance = classAttendanceRepo.findByClassSessionIdAndPersonId(classSessionId, personId);
        /* If student has arrived at session */
        if (classAttendance.isPresent()) {
            ClassAttendance attendance = classAttendance.get();


            if (attendance.getDepartureTime() != null) {
                /* If student has already ended his session */
                throw new RuntimeException("Student already attended this class session");
            } else {
                /* Student is ending his session now */
                attendance.setDepartureTime(LocalDateTime.now());
                return classAttendanceRepo.save(attendance);
            }
        } else {
            /* Student is starting his session now */
            StudentArrivalDto studentArrivalDto = new StudentArrivalDto();
            studentArrivalDto.setClassSessionId(classSessionId);
            studentArrivalDto.setPersonId(personId);
            studentArrivalDto.setSubjectName(classSession.getSubject().getName());
            studentArrivalDto.setFirstName(person.getFirstName());
            studentArrivalDto.setLastName(person.getLastName());
            studentArrivalDto.setArrivalTime(LocalDateTime.now().toString());
            studentArrivalDto.setMessage("Student has arrived at class session");

            simpMessagingTemplate.convertAndSend("/topic/class-session/" + classSessionId, studentArrivalDto);

            ClassAttendance attendance = new ClassAttendance();
            attendance.setClassSession(classSession);
            attendance.setPerson(person);
            attendance.setArrivalTime(LocalDateTime.now());
            return classAttendanceRepo.save(attendance);
        }
    }

    /*test method just print hello world*/
    public void helloWorld(){
        System.out.println("Hello World");
    }

}
