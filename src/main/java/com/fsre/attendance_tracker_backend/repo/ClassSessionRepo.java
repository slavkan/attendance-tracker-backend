package com.fsre.attendance_tracker_backend.repo;

import com.fsre.attendance_tracker_backend.model.ClassSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClassSessionRepo extends JpaRepository<ClassSession, Long> {
}
