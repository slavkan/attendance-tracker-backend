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
public class Faculty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String abbreviation;


    @OneToMany(mappedBy = "faculty", fetch = FetchType.LAZY)
    private Set<FacultyPerson> facultyPersons;

    @OneToMany(mappedBy = "faculty", fetch = FetchType.LAZY)
    private Set<Study> study;
}
