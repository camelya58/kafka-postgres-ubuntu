package com.github58.camelya.ubuntu.controller;

import com.github58.camelya.ubuntu.exception.NotFoundException;
import com.github58.camelya.ubuntu.model.Student;
import com.github58.camelya.ubuntu.repository.KafkaRepository;
import com.github58.camelya.ubuntu.repository.StudentRepository;
import com.github58.camelya.ubuntu.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Class StudentController is a simple RESt-Controller with CRUD operations.
 *
 * @author Kamila Meshcheryakova
 * created 22.06.2020
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class StudentController {

    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final KafkaRepository kafkaRepository;

    @GetMapping("/students")
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    @GetMapping("/student/{id}")
    public Student getStudentById(@PathVariable Long id) {
        Optional<Student> optStudent = studentRepository.findById(id);
        if (optStudent.isPresent()) {
            return optStudent.get();
        } else {
            throw new NotFoundException("Student not found with id " + id);
        }
    }

    @GetMapping("/students/age/{age}")
    public Set<Student> getStudentByAgeLessThan(@PathVariable int age) {
        Set<Student> optStudent = studentRepository.findStudentByAgeLessThan(age);
        if (!optStudent.isEmpty()) {
            return optStudent;
        } else {
            throw new NotFoundException("Students not found with age less than " + age);
        }
    }

    @GetMapping("/teacher/{teacherId}/students")
    public Set<Student> getStudentsByTeacherId(@PathVariable Long teacherId) {
        if (!teacherRepository.existsById(teacherId)) {
            throw new NotFoundException("Teacher not found!");
        }
        return studentRepository.findStudentsByTeacherId(teacherId);
    }

    @GetMapping("/students/city/{city}")
    public Set<Student> getStudentByCity(@PathVariable String city) {
        Set<Student> optStudent = studentRepository.findStudentsByAddressCity(city);
        if (!optStudent.isEmpty()) {
            return optStudent;
        } else {
            throw new NotFoundException("Students not found from " + city);
        }
    }

    @PostMapping("/student")
    public Student createStudent(@Valid @RequestBody Student student) {
        Student savedStudent = studentRepository.save(student);
        kafkaRepository.sendMessage(savedStudent.getId(), savedStudent);
        return savedStudent;
    }

    @PutMapping("/student/{id}")
    public Student updateStudent(@PathVariable Long id,
                                 @Valid @RequestBody Student studentUpdated) {
        return studentRepository.findById(id)
                .map(student -> {
                    student.setName(studentUpdated.getName());
                    student.setAge(studentUpdated.getAge());
                    return studentRepository.save(student);
                }).orElseThrow(() -> new NotFoundException("Student not found with id " + id));
    }

    @DeleteMapping("/student/{id}")
    public String deleteStudent(@PathVariable Long id) {
        return studentRepository.findById(id)
                .map(student -> {
                    studentRepository.delete(student);
                    return "Delete Successfully";
                }).orElseThrow(() -> new NotFoundException("Student not found with id " + id));
    }
}
