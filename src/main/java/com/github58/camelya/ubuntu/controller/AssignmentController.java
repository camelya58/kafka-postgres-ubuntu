package com.github58.camelya.ubuntu.controller;

import com.github58.camelya.ubuntu.exception.NotFoundException;
import com.github58.camelya.ubuntu.model.Assignment;
import com.github58.camelya.ubuntu.repository.AssignmentRepository;
import com.github58.camelya.ubuntu.repository.KafkaRepository;
import com.github58.camelya.ubuntu.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Class AssignmentController is a simple RESt-Controller with CRUD operations.
 *
 * @author Kamila Meshcheryakova
 * created 22.06.2020
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AssignmentController {

    private final AssignmentRepository assignmentRepository;
    private final StudentRepository studentRepository;
    private final KafkaRepository kafkaRepository;

    @GetMapping("/student/{studentId}/assignments")
    public List<Assignment> getContactByStudentId(@PathVariable Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new NotFoundException("Student not found!");
        }
        return assignmentRepository.findByStudentId(studentId);
    }

    @PostMapping("/student/{studentId}/assignment")
    public Assignment addAssignment(@PathVariable Long studentId,
                                    @Valid @RequestBody Assignment assignment) {
        Assignment savedAssignment = studentRepository.findById(studentId)
                .map(student -> {
                    assignment.setStudent(student);
                    return assignmentRepository.save(assignment);
                }).orElseThrow(() -> new NotFoundException("Student not found!"));
        kafkaRepository.sendMessage(savedAssignment.getId(), savedAssignment);
        return savedAssignment;
    }

    @PutMapping("/student/{studentId}/assignment/{assignmentId}")
    public Assignment updateAssignment(@PathVariable Long studentId,
                                       @PathVariable Long assignmentId,
                                       @Valid @RequestBody Assignment assignmentUpdated) {
        if (!studentRepository.existsById(studentId)) {
            throw new NotFoundException("Student not found!");
        }
        return assignmentRepository.findById(assignmentId)
                .map(assignment -> {
                    assignment.setName(assignmentUpdated.getName());
                    assignment.setGrade(assignmentUpdated.getGrade());
                    return assignmentRepository.save(assignment);
                }).orElseThrow(() -> new NotFoundException("Assignment not found!"));
    }

    @DeleteMapping("/student/{studentId}/assignment/{assignmentId}")
    public String deleteAssignment(@PathVariable Long studentId,
                                   @PathVariable Long assignmentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new NotFoundException("Student not found!");
        }
        return assignmentRepository.findById(assignmentId)
                .map(assignment -> {
                    assignmentRepository.delete(assignment);
                    return "Deleted Successfully!";
                }).orElseThrow(() -> new NotFoundException("Contact not found!"));
    }
}
