package com.fsre.attendance_tracker_backend.repo;

import com.fsre.attendance_tracker_backend.model.FacultyPerson;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FacultyPersonRepo extends JpaRepository<FacultyPerson, Long> {
    Optional<FacultyPerson> findByPersonIdAndFacultyId(Long personId, Long facultyId);
}
