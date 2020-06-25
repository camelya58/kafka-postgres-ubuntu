package com.github58.camelya.ubuntu.controller;

import com.github58.camelya.ubuntu.exception.NotFoundException;
import com.github58.camelya.ubuntu.model.Student;
import com.github58.camelya.ubuntu.model.Teacher;
import com.github58.camelya.ubuntu.repository.AssignmentRepository;
import com.github58.camelya.ubuntu.repository.KafkaRepository;
import com.github58.camelya.ubuntu.repository.StudentRepository;
import com.github58.camelya.ubuntu.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;
import java.util.Set;

/**
 * Class TeacherController is a simple RESt-Controller with CRUD operations.
 * todo add Exceptions AlreadyExist and to other controllers
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
        Optional<Teacher> optTeacher = teacherRepository.findById(id);
        if (optTeacher.isPresent()) {
            return optTeacher.get();
        } else {
            throw new NotFoundException("Teacher not found with id " + id);
        }
    }

    @PostMapping("/assignment/{assignmentId}/teacher")
    public Teacher addTeacherByAssignmentId(@PathVariable Long assignmentId,
                                    @Valid @RequestBody Teacher teacher) {

        Teacher savedTeacher = assignmentRepository.findById(assignmentId)
                .map(assignment -> {
                    teacher.setAssignment(assignment);
                    return teacherRepository.save(teacher);
                }).orElseThrow(() -> new NotFoundException("Assignment not found!"));
        Set<Student> students = studentRepository.findStudentsByAssignmentId(assignmentId);
        savedTeacher.setStudents(students);
        for (Student s: students) {
            s.addTeacher(savedTeacher);
            studentRepository.save(s);
        }
        kafkaRepository.sendMessage(savedTeacher.getId(), savedTeacher);
        return savedTeacher;
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
    public String deleteStudent (@PathVariable Long id) {
        return teacherRepository.findById(id)
                .map(teacher -> {
                    teacherRepository.delete(teacher);
                    return "Delete Successfully";
                }).orElseThrow(() -> new NotFoundException("Teacher not found with id " + id));
    }
}
