package com.fsre.attendance_tracker_backend.controller;

import com.fsre.attendance_tracker_backend.model.ClassSession;
import com.fsre.attendance_tracker_backend.model.Person;
import com.fsre.attendance_tracker_backend.model.Study;
import com.fsre.attendance_tracker_backend.model.Subject;
import com.fsre.attendance_tracker_backend.model.dto.ApiResponse;
import com.fsre.attendance_tracker_backend.repo.ClassSessionRepo;
import com.fsre.attendance_tracker_backend.repo.PersonRepo;
import com.fsre.attendance_tracker_backend.repo.SubjectRepo;
import com.fsre.attendance_tracker_backend.service.ClassSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/class-sessions")
@CrossOrigin
public class ClassSessionController {


    @Autowired
    private ClassSessionService classSessionService;


    @GetMapping("")
    public ResponseEntity<?> getClassSessionsBySubject(@RequestParam Long subjectId) {
        try{
            List<ClassSession> classSessions = classSessionService.getAllClassSessionsBySubject(subjectId);
            return new ResponseEntity<>(classSessions, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(new ApiResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getClassSessionById(@PathVariable Long id) {
        try{
            Optional<ClassSession> classSession = classSessionService.getClassSessionById(id);
            return new ResponseEntity<>(classSession, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(new ApiResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }


    @PostMapping("start")
    public ResponseEntity<?> startNewClassSession(@RequestParam Long subjectId, @RequestParam Long professor_id) {
        try {
            ClassSession classSession = classSessionService.startNewClassSession(subjectId, professor_id);
            return new ResponseEntity<>(classSession, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(new ApiResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("end")
    public ResponseEntity<?> endClassSession(@RequestParam Long classSessionId) {
        try {
            ClassSession classSession = classSessionService.endClassSession(classSessionId);
            return new ResponseEntity<>(classSession, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(new ApiResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }


}
