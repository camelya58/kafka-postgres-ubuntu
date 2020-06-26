package com.github58.camelya.ubuntu.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

/**
 * Class Teacher represents a POJO-object and an entity of the database.
 *
 * @author Kamila Meshcheryakova
 * created 23.06.2020
 */
@Getter
@Setter
@Entity
@Table(name = "teachers")
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Teacher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @OneToOne
    @JoinColumn(name = "assignment_id")
    private Assignment assignment;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "teachers")
    @JsonIgnore
    private Set<Student> students;
}
