package com.github58.camelya.ubuntu.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

/**
 * Class Student represents a POJO-object and an entity of the database.
 *
 * @author Kamila Meshcheryakova
 * created 22.06.2020
 */
@Getter
@Setter
@Entity
@Table(name = "students")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@SuppressWarnings("unused")
public class Student implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "age")
    private int age;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Assignment> assignments;

    public Student() {}

    public Student(String name, int age) {
        this.name = name;
        this.age = age;
    }
}
