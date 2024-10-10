package com.fsre.attendance_tracker_backend.controller;

import com.fsre.attendance_tracker_backend.model.Study;
import com.fsre.attendance_tracker_backend.model.dto.ApiResponse;
import com.fsre.attendance_tracker_backend.service.StudyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/study")
@CrossOrigin
public class StudyController {

    @Autowired
    private StudyService studyService;


    @GetMapping("")
    public ResponseEntity<?> getStudiesForFaculty(@RequestParam Long facultyId) {
        try{
            List<Study> studies = studyService.getAllStudiesByFaculty(facultyId);
            return new ResponseEntity<>(studies, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(new ApiResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /*Get single study by id*/
    @GetMapping("/{id}")
    public ResponseEntity<?> getStudyById(@PathVariable Long id) {
        try {
            Study study = studyService.getStudyById(id);
            return new ResponseEntity<>(study, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(new ApiResponse(e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("")
    public ResponseEntity<?> addStudy(@RequestBody Study study) {
        try {
            studyService.addStudy(study);
            return new ResponseEntity<>(study, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(new ApiResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/{id}")
    public ResponseEntity<?> updateStudy(@PathVariable Long id, @RequestBody Study study) {
        try {
            Study updatedStudy = studyService.updateStudy(id, study);
            return new ResponseEntity<>(updatedStudy, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStudy(@PathVariable Long id) {
        try {
            ApiResponse response = studyService.deleteStudy(id);
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
