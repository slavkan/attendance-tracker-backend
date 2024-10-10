package com.fsre.attendance_tracker_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ClassSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClassSessionState state;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column
    private LocalDateTime endTime;

    @Column
    private String codeForArrival;

    @Column
    private String codeForArrivalPrevious;

    /* All times (arrival, departure, endTime) will be offset by X minutes
    Helps for testing application, not intended for production */
    @Column
    private Long offsetInMinutes;

    @ManyToOne
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @ManyToOne
    @JoinColumn(name = "professor_id", nullable = false)
    private Person person;

    @OneToMany(mappedBy = "classSession", fetch = FetchType.LAZY)
    private Set<ClassAttendance> classAttendances;

}
