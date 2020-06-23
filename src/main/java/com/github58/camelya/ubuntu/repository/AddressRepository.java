package com.github58.camelya.ubuntu.repository;

import com.github58.camelya.ubuntu.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Interface AddressRepository represents connection to PostgreSQL.
 *
 * @author Kamila Meshcheryakova
 * created 23.06.2020
 */
@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    Address findAddressByStudentId(Long studentId);
}
