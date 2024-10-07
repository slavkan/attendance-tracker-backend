package com.fsre.attendance_tracker_backend.repo.specification;

import com.fsre.attendance_tracker_backend.model.Person;
import org.springframework.data.jpa.domain.Specification;

public class PersonSpecifications {

    public static Specification<Person> hasFirstName(String firstName) {
        return (root, query, criteriaBuilder) -> {
            if (firstName == null) {
                return null;
            }
            String pattern = "%" + firstName + "%";
            return criteriaBuilder.like(root.get("firstName"), pattern);
        };
    }

    public static Specification<Person> hasLastName(String lastName) {
        return (root, query, criteriaBuilder) -> {
            if (lastName == null) {
                return null;
            }
            String pattern = "%" + lastName + "%";
            return criteriaBuilder.like(root.get("lastName"), pattern);
        };
    }

    public static Specification<Person> hasRole(String role) {
        return (root, query, criteriaBuilder) -> {
            if (role == null) {
                return null;
            }
            switch (role) {
                case "admin":
                    return criteriaBuilder.isTrue(root.get("isAdmin"));
                case "worker":
                    return criteriaBuilder.isTrue(root.get("isWorker"));
                case "professor":
                    return criteriaBuilder.isTrue(root.get("isProfessor"));
                case "student":
                    return criteriaBuilder.isTrue(root.get("isStudent"));
                default:
                    return null;
            }
        };
    }

    public static Specification<Person> hasFacultyId(Long facultyId) {
        return (root, query, criteriaBuilder) -> {
            if (facultyId == null) {
                return null;
            }
            return criteriaBuilder.equal(root.join("facultyPersons").get("faculty").get("id"), facultyId);
        };
    }

}
