package com.fsre.attendance_tracker_backend.repo;

import com.fsre.attendance_tracker_backend.model.SubjectPerson;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubjectPersonRepo extends JpaRepository<SubjectPerson, Long> {

    Optional<SubjectPerson> findByPersonIdAndSubjectId(Long personId, Long subjectId);

    List<SubjectPerson> findByPersonId(Long personId);

}
