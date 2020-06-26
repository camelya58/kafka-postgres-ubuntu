package com.github58.camelya.ubuntu.repository;

import com.github58.camelya.ubuntu.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

/**
 * Interface TeacherRepository represents connection to PostgreSQL.
 *
 * @author Kamila Meshcheryakova
 * created 23.06.2020
 */
@Repository
@SuppressWarnings("unconfigured")
public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    @Modifying
    @Query(value = "SELECT t.id, t.name, t.assignment_id FROM teachers t left outer JOIN student_teacher st " +
            "ON t.id=st.teacher_id where st.student_id = :studentId", nativeQuery = true)
    Set<Teacher> findTeachersByStudentId(Long studentId);
}