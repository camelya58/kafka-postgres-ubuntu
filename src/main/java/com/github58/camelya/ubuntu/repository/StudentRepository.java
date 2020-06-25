package com.github58.camelya.ubuntu.repository;

import com.github58.camelya.ubuntu.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

/**
 * Interface StudentRepository represents connection to PostgreSQL.
 *
 * @author Kamila Meshcheryakova
 * created 22.06.2020
 */
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Set<Student> findStudentByAgeLessThan(int age);

    Set<Student> findStudentsByAddressCity(String city);

    @Modifying
    @Query(value = "SELECT s.id, s.age, s.name FROM students s left outer JOIN student_teacher t " +
            "ON s.id=t.student_id where t.teacher_id = :teacherId", nativeQuery = true)
    Set<Student> findStudentsByTeacherId(Long teacherId);

    @Modifying
    @Query(value = "SELECT s.id, s.age, s.name FROM students s left outer JOIN assignments a " +
            "ON s.id=a.student_id where a.id = :assignmentId", nativeQuery = true)
    Set<Student> findStudentsByAssignmentId(Long assignmentId);

    @Modifying
    @Query(value = "SELECT s.id, s.age, s.name FROM students s left outer JOIN assignments a " +
            "ON s.id=a.student_id where a.name = :assignmentName", nativeQuery = true)
    Set<Student> findStudentsByAssignmentName(String assignmentName);
}
//   SELECT s.id, s.age, s.name FROM Student s JOIN Assignment a " +
// "ON s.id=a.student.id where a.id = :assignmentId
