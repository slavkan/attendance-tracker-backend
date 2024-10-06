package com.fsre.attendance_tracker_backend.repo;

import com.fsre.attendance_tracker_backend.model.Study;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudyRepo extends JpaRepository<Study, Long> {

    List<Study> findByFacultyId(Long personId);
}
