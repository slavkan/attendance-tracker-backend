package com.fsre.attendance_tracker_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int semester;

    @ManyToOne
    @JoinColumn(name = "study_id", nullable = false)
    private Study study;

    @OneToMany(mappedBy = "subject", fetch = FetchType.LAZY)
    private Set<SubjectPerson> subjectPeople;

    @OneToMany(mappedBy = "subject", fetch = FetchType.LAZY)
    private Set<ClassSession> classSessions;

}

