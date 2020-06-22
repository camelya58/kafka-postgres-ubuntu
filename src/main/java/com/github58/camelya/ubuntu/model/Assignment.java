package com.github58.camelya.ubuntu.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Class Assignment represents a POJO-object and an entity of the database.
 *
 * @author Kamila Meshcheryakova
 * created 22.06.2020
 */
@Getter
@Setter
@Entity
@Table(name = "assignments")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@SuppressWarnings("unused")
public class Assignment implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "grade")
    private int grade;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    @JsonIgnore
    private Student student;

    public Assignment() {}

    public Assignment(String name, int grade) {
        this.name = name;
        this.grade = grade;
    }
}
