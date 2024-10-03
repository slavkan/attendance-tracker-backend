package com.fsre.attendance_tracker_backend.controller;

import com.fsre.attendance_tracker_backend.model.Faculty;
import com.fsre.attendance_tracker_backend.model.FacultyPerson;
import com.fsre.attendance_tracker_backend.model.dto.ApiResponse;
import com.fsre.attendance_tracker_backend.service.FacultyPersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/faculty-person")
@CrossOrigin
public class FacultyPersonController {

    @Autowired
    private FacultyPersonService facultyPersonService;

    public FacultyPersonController(FacultyPersonService facultyPersonService) {
        this.facultyPersonService = facultyPersonService;
    }

    @PostMapping("")
    public ResponseEntity<?> assignPersonToFaculty(@RequestParam Long personId, @RequestParam Long facultyId) {
        try {
            FacultyPerson facultyPerson = facultyPersonService.assignPersonToFaculty(personId, facultyId);
            return new ResponseEntity<>(facultyPerson, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(new ApiResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /* Get all faculties person is connected to */
    @GetMapping("")
    public ResponseEntity<?> getPersonFaculties(@RequestParam Long personId) {
        try {
            List<FacultyPerson> faculties = facultyPersonService.getAllFacultiesByPerson(personId);
            return new ResponseEntity<>(faculties, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(new ApiResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> removePersonFromFaculty(@PathVariable Long id) {
        try {
            ApiResponse response = facultyPersonService.removePersonFromFaculty(id);
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
