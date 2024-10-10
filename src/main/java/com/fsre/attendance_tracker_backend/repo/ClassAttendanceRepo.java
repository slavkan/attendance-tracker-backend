package com.fsre.attendance_tracker_backend.repo;

import com.fsre.attendance_tracker_backend.model.ClassAttendance;
import com.fsre.attendance_tracker_backend.model.ClassSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClassAttendanceRepo extends JpaRepository<ClassAttendance, Long> {

    List<ClassAttendance> findByClassSessionId(Long classSessionId);

    Optional<ClassAttendance> findByClassSessionIdAndPersonId(Long classSessionId, Long personId);

}
