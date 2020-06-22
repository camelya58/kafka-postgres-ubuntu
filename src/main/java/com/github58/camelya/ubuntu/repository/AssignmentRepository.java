package com.github58.camelya.ubuntu.repository;

import com.github58.camelya.ubuntu.model.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Класс AssignmentRepository
 *
 * @author Kamila Meshcheryakova
 * created 22.06.2020
 */
@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    List<Assignment> findByStudentId(Long studentId);
}
