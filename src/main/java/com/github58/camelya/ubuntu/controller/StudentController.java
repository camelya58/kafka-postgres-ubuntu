package com.github58.camelya.ubuntu.controller;

import com.github58.camelya.ubuntu.exception.NotFoundException;
import com.github58.camelya.ubuntu.model.Student;
import com.github58.camelya.ubuntu.repository.KafkaRepository;
import com.github58.camelya.ubuntu.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

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

    @GetMapping("/student/{age}")
    public Student getStudentByAgeLessThan(@PathVariable int age) {
        Optional<Student> optStudent = studentRepository.findStudentByAgeLessThan(age);
        if (optStudent.isPresent()) {
            return optStudent.get();
        } else {
            throw new NotFoundException("Student not found with age less than " + age);
        }
    }

    @PostMapping("/student")
    public Student createStudent(@Valid @RequestBody Student student) {
        Student savedStudent = studentRepository.save(student);
        kafkaRepository.sendMessage(savedStudent.getId(), savedStudent);
        return savedStudent;
    }

    @PutMapping("/student/{id}")
    public Student updateStudent (@PathVariable Long id,
                                  @Valid @RequestBody Student studentUpdated) {
        return studentRepository.findById(id)
                .map(student -> {
                    student.setName(studentUpdated.getName());
                    student.setAge(studentUpdated.getAge());
                    return studentRepository.save(student);
                }).orElseThrow(() -> new NotFoundException("Student not found with id " + id));
    }

    @DeleteMapping("/student/{id}")
    public String deleteStudent (@PathVariable Long id) {
        return studentRepository.findById(id)
                .map(student -> {
                    studentRepository.delete(student);
                    return "Delete Successfully";
                }).orElseThrow(() -> new NotFoundException("Student not found with id " + id));
    }
}
