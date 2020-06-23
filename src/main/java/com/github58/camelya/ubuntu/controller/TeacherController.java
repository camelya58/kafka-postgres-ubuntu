package com.github58.camelya.ubuntu.controller;

import com.github58.camelya.ubuntu.exception.NotFoundException;
import com.github58.camelya.ubuntu.model.Teacher;
import com.github58.camelya.ubuntu.repository.KafkaRepository;
import com.github58.camelya.ubuntu.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

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
    private final KafkaRepository kafkaRepository;

    @GetMapping("/teachers")
    public List<Teacher> getAllTeachers() {
        return teacherRepository.findAll();
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

    @PostMapping("/teacher")
    public Teacher addTeacher(@RequestBody Teacher teacher) {
        Teacher saved = teacherRepository.save(teacher);
        kafkaRepository.sendMessage(teacher.getId(), saved);
        return saved;
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
