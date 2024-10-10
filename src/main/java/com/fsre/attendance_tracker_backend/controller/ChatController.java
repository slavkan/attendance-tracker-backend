package com.fsre.attendance_tracker_backend.controller;

import com.fsre.attendance_tracker_backend.model.*;
import com.fsre.attendance_tracker_backend.repo.ClassAttendanceRepo;
import com.fsre.attendance_tracker_backend.repo.ClassSessionRepo;
import com.fsre.attendance_tracker_backend.repo.PersonRepo;
import com.fsre.attendance_tracker_backend.repo.SubjectPersonRepo;
import com.fsre.attendance_tracker_backend.service.ClassAttendanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fsre.attendance_tracker_backend.model.dto.StudentArrivalDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Controller
public class ChatController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private ClassAttendanceService classAttendanceService;

    @Autowired
    private ClassAttendanceRepo classAttendanceRepo;

    @Autowired
    private PersonRepo personRepo;

    @Autowired
    private ClassSessionRepo classSessionRepo;

    @Autowired
    private SubjectPersonRepo subjectPersonRepo;

    Logger logger = LoggerFactory.getLogger(ChatController.class);

    @MessageMapping("/message")
    @SendTo("/chatroom/public")
    public Message receiveMessage(@Payload Message message){
        Logger logger = LoggerFactory.getLogger(ChatController.class);
        return message;
    }

    @MessageMapping("/private-message")
    public Message recMessage(@Payload Message message){
        simpMessagingTemplate.convertAndSendToUser(message.getReceiverName(),"/private",message);
        return message;
    }



    @MessageMapping("/class-session")
    public StudentArrivalDto receiveMessage(@Payload StudentArrivalDto studentArrivalDto) {
        Long personId = studentArrivalDto.getPersonId();
        Long classSessionId = studentArrivalDto.getClassSessionId();
        String code = studentArrivalDto.getCode();


        StudentArrivalDto message = new StudentArrivalDto();
        message.setClassSessionId(studentArrivalDto.getClassSessionId());
        message.setPersonId(personId);

        Person person = personRepo.findById(personId).orElse(null);
        if (person == null) {
            message.setFirstName("Unknown");
            message.setLastName("Unknown");
            message.setMessage("Person not found");
            simpMessagingTemplate.convertAndSendToUser(studentArrivalDto.getClassSessionId().toString(),"/private", message);
            simpMessagingTemplate.convertAndSendToUser(studentArrivalDto.getClassSessionId().toString(),"/private-student", message);
            return message;
        }
        message.setFirstName(person.getFirstName());
        message.setLastName(person.getLastName());

        ClassSession classSession = classSessionRepo.findById(classSessionId).orElse(null);
        if (classSession == null) {
            message.setMessage("Class Session not found");
            simpMessagingTemplate.convertAndSendToUser(studentArrivalDto.getClassSessionId().toString(),"/private", message);
            simpMessagingTemplate.convertAndSendToUser(studentArrivalDto.getClassSessionId().toString(),"/private-student", message);
            return message;
        }
        message.setSubjectName(classSession.getSubject().getName());

        Long subjectId = classSession.getSubject().getId();
        if (subjectPersonRepo.findByPersonIdAndSubjectId(personId, subjectId).isEmpty()) {
            message.setMessage("Student is not part of this subject");
            simpMessagingTemplate.convertAndSendToUser(studentArrivalDto.getClassSessionId().toString(),"/private", message);
            simpMessagingTemplate.convertAndSendToUser(studentArrivalDto.getClassSessionId().toString(),"/private-student", message);
            return message;
        }
        if (classSession.getState() != ClassSessionState.IN_PROGRESS) {
            message.setMessage("Class session is not in progress");
            simpMessagingTemplate.convertAndSendToUser(studentArrivalDto.getClassSessionId().toString(),"/private", message);
            simpMessagingTemplate.convertAndSendToUser(studentArrivalDto.getClassSessionId().toString(),"/private-student", message);
            return message;
        }
        if (!Objects.equals(classSession.getCodeForArrival(), code) && !Objects.equals(classSession.getCodeForArrivalPrevious(), code)) {
            message.setMessage("Invalid code");
            simpMessagingTemplate.convertAndSendToUser(studentArrivalDto.getClassSessionId().toString(),"/private", message);
            simpMessagingTemplate.convertAndSendToUser(studentArrivalDto.getClassSessionId().toString(),"/private-student", message);
            return message;
        }

        Optional<ClassAttendance> classAttendance = classAttendanceRepo.findByClassSessionIdAndPersonId(classSessionId, personId);
        if (classAttendance.isPresent()) {
            ClassAttendance attendance = classAttendance.get();
            if (attendance.getDepartureTime() != null) {
                message.setMessage("Student already attended this class session");
                simpMessagingTemplate.convertAndSendToUser(studentArrivalDto.getClassSessionId().toString(),"/private", message);
                simpMessagingTemplate.convertAndSendToUser(studentArrivalDto.getClassSessionId().toString(),"/private-student", message);
                return message;
            } else {
                attendance.setDepartureTime(LocalDateTime.now());
                classAttendanceRepo.save(attendance);
                message.setMessage("Student has departed from class session");
                simpMessagingTemplate.convertAndSendToUser(studentArrivalDto.getClassSessionId().toString(),"/private", message);
                simpMessagingTemplate.convertAndSendToUser(studentArrivalDto.getClassSessionId().toString(),"/private-student", message);
                return message;
            }
        } else {
            message.setArrivalTime(LocalDateTime.now().toString());
            message.setMessage("Student has arrived at class session");
            simpMessagingTemplate.convertAndSendToUser(studentArrivalDto.getClassSessionId().toString(),"/private", message);
            simpMessagingTemplate.convertAndSendToUser(studentArrivalDto.getClassSessionId().toString(),"/private-student", message);
            ClassAttendance attendance = new ClassAttendance();
            attendance.setClassSession(classSession);
            attendance.setPerson(person);
            attendance.setArrivalTime(LocalDateTime.now());
            classAttendanceRepo.save(attendance);
            return message;
        }
    }



}

