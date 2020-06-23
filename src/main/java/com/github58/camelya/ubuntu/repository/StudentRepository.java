package com.github58.camelya.ubuntu.repository;

import com.github58.camelya.ubuntu.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Interface StudentRepository represents connection to PostgreSQL.
 *
 * @author Kamila Meshcheryakova
 * created 22.06.2020
 */
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findStudentByAgeLessThan(int age);
}
