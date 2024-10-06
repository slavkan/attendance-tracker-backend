package com.fsre.attendance_tracker_backend.controller;


import com.fsre.attendance_tracker_backend.model.Study;
import com.fsre.attendance_tracker_backend.model.Subject;
import com.fsre.attendance_tracker_backend.model.dto.ApiResponse;
import com.fsre.attendance_tracker_backend.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/subjects")
@CrossOrigin
public class SubjectController {

    @Autowired
    private SubjectService subjectService;

    @GetMapping("")
    public ResponseEntity<?> getSubjectsForStudy(@RequestParam Long studyId) {
        try{
            List<Subject> subjects = subjectService.getAllSubjectsByStudy(studyId);
            return new ResponseEntity<>(subjects, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(new ApiResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("")
    public ResponseEntity<?> addSubject(@RequestBody Subject subject) {
        try {
            subjectService.addSubject(subject);
            return new ResponseEntity<>(subject, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(new ApiResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateSubject(@PathVariable Long id, @RequestBody Subject subject) {
        try {
            Subject updatedSubject = subjectService.updateSubject(id, subject);
            return new ResponseEntity<>(updatedSubject, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSubject(@PathVariable Long id) {
        try {
            ApiResponse response = subjectService.deleteSubject(id);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("foreign key constraints")) {
                return new ResponseEntity<>(new ApiResponse(e.getMessage()), HttpStatus.CONFLICT);
            } else {
                return new ResponseEntity<>(new ApiResponse(e.getMessage()), HttpStatus.NOT_FOUND);
            }
        }
    }



}
