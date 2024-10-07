package com.fsre.attendance_tracker_backend.controller;

import com.fsre.attendance_tracker_backend.model.SubjectPerson;
import com.fsre.attendance_tracker_backend.model.dto.ApiResponse;
import com.fsre.attendance_tracker_backend.service.SubjectPersonService;
import com.fsre.attendance_tracker_backend.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subject-person")
@CrossOrigin
public class SubjectPersonController {

    @Autowired
    SubjectPersonService subjectPersonService;

    public SubjectPersonController(SubjectPersonService subjectPersonService) {
        this.subjectPersonService = subjectPersonService;
    }

    @PostMapping("")
    public ResponseEntity<?> assignPersonToSubject(@RequestParam Long personId, @RequestParam Long subjectId) {
        try {
            SubjectPerson subjectPerson = subjectPersonService.assignPersonToSubject(personId, subjectId);
            return new ResponseEntity<>(subjectPerson, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(new ApiResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /* Get all subjects person is connected to */
    @GetMapping("")
    public ResponseEntity<?> getPersonSubjects(@RequestParam Long personId) {
        try {
            List<SubjectPerson> subjects = subjectPersonService.getAllSubjectsByPerson(personId);
            return new ResponseEntity<>(subjects, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(new ApiResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("")
    public ResponseEntity<?> removePersonFromSubject(@RequestParam Long personId, @RequestParam Long subjectId) {
        try {
            ApiResponse response = subjectPersonService.removePersonFromSubject(personId, subjectId);
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
