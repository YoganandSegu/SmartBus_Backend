package com.smartbus.repository;

import com.smartbus.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DriverRepository extends JpaRepository<Driver, Long> {
    Optional<Driver> findByPhone(String phone);
    Optional<Driver> findByUserUsername(String username);
    boolean existsByPhone(String phone);
    boolean existsByBusId(Long busId);
}
