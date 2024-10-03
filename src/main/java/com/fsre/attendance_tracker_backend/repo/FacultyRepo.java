package com.fsre.attendance_tracker_backend.repo;

import com.fsre.attendance_tracker_backend.model.Faculty;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FacultyRepo extends JpaRepository<Faculty, Long> {
}
