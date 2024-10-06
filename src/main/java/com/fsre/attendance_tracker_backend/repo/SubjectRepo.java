package com.fsre.attendance_tracker_backend.repo;

import com.fsre.attendance_tracker_backend.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubjectRepo extends JpaRepository<Subject, Long> {

    List<Subject> findByStudyId(Long studyId);
}
