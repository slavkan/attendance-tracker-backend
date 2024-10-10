package com.fsre.attendance_tracker_backend.controller;

import com.fsre.attendance_tracker_backend.model.ClassAttendance;
import com.fsre.attendance_tracker_backend.model.dto.ApiResponse;
import com.fsre.attendance_tracker_backend.service.ClassAttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/class-attendance")
@CrossOrigin
public class ClassAttendanceController {

    @Autowired
    private ClassAttendanceService classAttendanceService;


    @GetMapping("/{id}")
    public ResponseEntity<?> getClassAttendancesForSession(@PathVariable Long id) {
        try {
            Iterable<ClassAttendance> attendances = classAttendanceService.getClassAttendancesForSession(id);
            return new ResponseEntity<>(attendances, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/student-arrival")
    public ResponseEntity<?> studentArrival(@RequestParam Long classSessionId, @RequestParam Long personId, @RequestParam String code) {
        try{
            ClassAttendance classAttendance = classAttendanceService.studentToAttendance(classSessionId, personId, code);
            return new ResponseEntity<>(classAttendance, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
