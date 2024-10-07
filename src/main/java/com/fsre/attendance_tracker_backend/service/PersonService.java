package com.fsre.attendance_tracker_backend.service;

import com.fsre.attendance_tracker_backend.model.Person;
import com.fsre.attendance_tracker_backend.model.Study;
import com.fsre.attendance_tracker_backend.model.dto.ApiResponse;
import com.fsre.attendance_tracker_backend.model.dto.LoginRequestDto;
import com.fsre.attendance_tracker_backend.model.dto.PersonFilterDto;
import com.fsre.attendance_tracker_backend.repo.PersonRepo;
import com.fsre.attendance_tracker_backend.repo.specification.PersonSpecifications;
import com.fsre.attendance_tracker_backend.utils.PasswordGeneratorUtil;
import com.fsre.attendance_tracker_backend.utils.PasswordValidatorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;

@Service
public class PersonService {

    @Autowired
    private PersonRepo personRepo;

    @Autowired
    private EmailService emailService;

    @Autowired
    private JWTService jwtService;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    public List<Person> getAllPersons() {
        return personRepo.findAll();
    }

    public Person getPerson(Long id) {
        return personRepo.findById(id).orElse(null);
    }

    public Person addPerson(Person person) {
        if (personRepo.existsByEmail(person.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        if (personRepo.existsByUsername(person.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        String generatedPassword = PasswordGeneratorUtil.generateNewPassword();
        System.out.println("NEW PERSON PASSWORD: " + generatedPassword);
        person.setPassword(encoder.encode(generatedPassword));
        return personRepo.save(person);
    }

    public Person updatePerson(Long id, Person person) {
        Optional<Person> existingPersonOptional = personRepo.findById(id);
        if (existingPersonOptional.isPresent()) {
            Person existingPerson = existingPersonOptional.get();

            existingPerson.setFirstName(person.getFirstName());
            existingPerson.setLastName(person.getLastName());
            existingPerson.setUsername(person.getUsername());
            existingPerson.setEmail(person.getEmail());
            existingPerson.setIndexNumber(person.getIndexNumber());
            existingPerson.setAcademicTitle(person.getAcademicTitle());
            existingPerson.setAdmin(person.isAdmin());
            existingPerson.setWorker(person.isWorker());
            existingPerson.setProfessor(person.isProfessor());
            existingPerson.setStudent(person.isStudent());

            return personRepo.save(existingPerson);
        } else {
            throw new RuntimeException("Person not found with id " + id);
        }
    }

    public ApiResponse deletePerson(Long id) {
        Optional<Person> existingPersonOptional = personRepo.findById(id);
        if (existingPersonOptional.isPresent()) {
            try {
                personRepo.deleteById(id);
                return new ApiResponse("User deleted successfully");
            } catch (DataIntegrityViolationException e) {
                throw new RuntimeException("Cannot delete user due to foreign key constraints");
            }
        } else {
            throw new RuntimeException("User not found with id " + id);
        }
    }

    /* FILTERED PERSONS */
    public Page<Person> getFilteredPersons(PersonFilterDto filter, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Specification<Person> spec = Specification.where(PersonSpecifications.hasFirstName(filter.getFirstName()))
                .and(PersonSpecifications.hasLastName(filter.getLastName()))
                .and(PersonSpecifications.hasRole(filter.getRole()))
                .and(PersonSpecifications.hasFacultyId(filter.getFacultyId()));

        return personRepo.findAll(spec, pageable);
    }

}
