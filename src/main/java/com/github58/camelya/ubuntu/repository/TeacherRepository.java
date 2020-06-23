package com.github58.camelya.ubuntu.repository;

import com.github58.camelya.ubuntu.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Interface TeacherRepository represents connection to PostgreSQL.
 *
 * @author Kamila Meshcheryakova
 * created 23.06.2020
 */
@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {
}
