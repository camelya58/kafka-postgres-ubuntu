package com.github58.camelya.ubuntu.controller;

import com.github58.camelya.ubuntu.exception.AlreadyExistsException;
import com.github58.camelya.ubuntu.exception.NotFoundException;
import com.github58.camelya.ubuntu.model.Assignment;
import com.github58.camelya.ubuntu.model.Student;
import com.github58.camelya.ubuntu.model.Teacher;
import com.github58.camelya.ubuntu.repository.AssignmentRepository;
import com.github58.camelya.ubuntu.repository.KafkaRepository;
import com.github58.camelya.ubuntu.repository.StudentRepository;
import com.github58.camelya.ubuntu.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Set;

/**
 * Class TeacherController is a simple RESt-Controller with CRUD operations.
 *
 * @author Kamila Meshcheryakova
 * created 23.06.2020
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherRepository teacherRepository;
    private final AssignmentRepository assignmentRepository;
    private final StudentRepository studentRepository;
    private final KafkaRepository kafkaRepository;

    @GetMapping("/student/{studentId}/teachers")
    public Set<Teacher> getTeachersByStudentId(@PathVariable Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new NotFoundException("Student not found!");
        }
        return teacherRepository.findTeachersByStudentId(studentId);
    }

    @GetMapping("/teacher/{id}")
    public Teacher getTeacherById(@PathVariable Long id) {
        return teacherRepository.findById(id).
                orElseThrow(() -> new NotFoundException("Teacher not found with id " + id));
    }

    @PostMapping("/assignment/{assignmentId}/teacher")
    public Teacher addTeacherByAssignmentId(@PathVariable Long assignmentId,
                                            @Valid @RequestBody Teacher teacher) {

        Assignment assignment = assignmentRepository.findById(assignmentId).
                orElseThrow(() -> new NotFoundException("Assignment not found!"));
        if (assignment.getTeacher() == null) {
            teacher.setAssignment(assignment);
            Teacher savedTeacher = teacherRepository.save(teacher);
            Set<Student> students = studentRepository.findStudentsByAssignmentName(assignment.getName());
            savedTeacher.setStudents(students);
            for (Student s : students) {
                s.addTeacher(savedTeacher);
                studentRepository.save(s);
            }
            kafkaRepository.sendMessage(savedTeacher.getId(), savedTeacher);
            return savedTeacher;
        } else throw new AlreadyExistsException("This assignment has already had a teacher!");
    }

    @PutMapping("/teacher/{id}")
    public Teacher updateTeacher(@PathVariable Long id,
                                 @Valid @RequestBody Teacher teacherUpdated) {
        return teacherRepository.findById(id)
                .map(teacher -> {
                    teacher.setName(teacherUpdated.getName());
                    return teacherRepository.save(teacher);
                }).orElseThrow(() -> new NotFoundException("Teacher not found with id " + id));
    }

    @DeleteMapping("/teacher/{id}")
    public String deleteStudent(@PathVariable Long id) {
        return teacherRepository.findById(id)
                .map(teacher -> {
                    teacherRepository.delete(teacher);
                    return "Delete Successfully";
                }).orElseThrow(() -> new NotFoundException("Teacher not found with id " + id));
    }
}
