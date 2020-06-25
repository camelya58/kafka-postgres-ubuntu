package com.github58.camelya.ubuntu.controller;

import com.github58.camelya.ubuntu.exception.NotFoundException;
import com.github58.camelya.ubuntu.model.Address;
import com.github58.camelya.ubuntu.repository.AddressRepository;
import com.github58.camelya.ubuntu.repository.KafkaRepository;
import com.github58.camelya.ubuntu.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Class AddressController is a simple RESt-Controller with CRUD operations.
 *
 * @author Kamila Meshcheryakova
 * created 23.06.2020
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AddressController {
    private final AddressRepository addressRepository;
    private final StudentRepository studentRepository;
    private final KafkaRepository kafkaRepository;

    @GetMapping("/student/{studentId}/address")
    public Address getAddressByStudentId(@PathVariable Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new NotFoundException("Student not found!");
        }
        return addressRepository.findAddressByStudentId(studentId);
    }

    @PostMapping("/student/{studentId}/address")
    public Address addAddress(@PathVariable Long studentId,
                                    @Valid @RequestBody Address address) {
        Address savedAddress = studentRepository.findById(studentId)
                .map(student -> {
                    address.setStudent(student);
                    return addressRepository.save(address);
                }).orElseThrow(() -> new NotFoundException("Student not found!"));
        kafkaRepository.sendMessage(savedAddress.getId(), savedAddress);
        return savedAddress;
    }

    @PutMapping("/student/{studentId}/address/{addressId}")
    public Address updateAddress(@PathVariable Long studentId,
                                       @PathVariable Long addressId,
                                       @Valid @RequestBody Address addressUpdated) {
        if(!studentRepository.existsById(studentId)) {
            throw new NotFoundException("Student not found!");
        }
        return addressRepository.findById(addressId)
                .map(address -> {
                    address.setCountry(addressUpdated.getCountry());
                    address.setCity(addressUpdated.getCity());
                    address.setStreet(addressUpdated.getStreet());
                    address.setHomeNumber(addressUpdated.getHomeNumber());
                    address.setFlatNumber(addressUpdated.getFlatNumber());
                    return addressRepository.save(address);
                }).orElseThrow(() -> new NotFoundException("Address not found!"));
    }

    @DeleteMapping("/student/{studentId}/address{addressId}")
    public String deleteAddress(@PathVariable Long studentId,
                                   @PathVariable Long addressId) {
        if(!studentRepository.existsById(studentId)) {
            throw new NotFoundException("Student not found!");
        }
        return addressRepository.findById(addressId)
                .map(address -> {
                    addressRepository.delete(address);
                    return "Deleted Successfully!";
                }).orElseThrow(() -> new NotFoundException("Address not found!"));
    }
}

