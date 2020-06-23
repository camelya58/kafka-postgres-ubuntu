package com.github58.camelya.ubuntu.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;

/**
 * Class Address represents a POJO-object and a table named "addresses" in the database.
 *
 * @author Kamila Meshcheryakova
 * created 23.06.2020
 */
@Data
@Entity
@Table(name = "addresses")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "country")
    private String country;

    @Column(name = "city")
    private String city;

    @Column(name = "street")
    private String street;

    @Column(name = "home_number")
    private int homeNumber;

    @Column(name = "flat_number")
    private int flatNumber;

    @OneToOne(mappedBy = "address")
    @JsonIgnore
    private Student student;
}
