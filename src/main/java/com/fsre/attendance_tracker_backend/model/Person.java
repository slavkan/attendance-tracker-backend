package com.fsre.attendance_tracker_backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fsre.attendance_tracker_backend.types.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;

    private String lastName;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String username;

    @JsonIgnore
    private String password;

    private String indexNumber;

    private String academicTitle;

    @Column(nullable = false)
    private boolean isAdmin;

    @Column(nullable = false)
    private boolean isWorker;

    @Column(nullable = false)
    private boolean isProfessor;

    @Column(nullable = false)
    private boolean isStudent;
}
