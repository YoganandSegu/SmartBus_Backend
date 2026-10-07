package com.smartbus.service;

import com.smartbus.dto.DriverRequest;
import com.smartbus.dto.DriverResponse;
import com.smartbus.entity.Bus;
import com.smartbus.entity.Driver;
import com.smartbus.entity.User;
import com.smartbus.exception.ResourceNotFoundException;
import com.smartbus.repository.BusRepository;
import com.smartbus.repository.DriverRepository;
import com.smartbus.repository.UserRepository;
import com.smartbus.util.PhoneNumbers;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DriverService {
    private final DriverRepository drivers;
    private final UserRepository users;
    private final BusRepository buses;
    private final PasswordEncoder passwordEncoder;

    public DriverService(DriverRepository drivers, UserRepository users, BusRepository buses,
                        PasswordEncoder passwordEncoder) {
        this.drivers = drivers;
        this.users = users;
        this.buses = buses;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<DriverResponse> findAll() {
        return drivers.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional
    public DriverResponse create(DriverRequest request) {
        String phone = PhoneNumbers.normalize(request.phone());
        boolean usernameAlreadyUsesPhone = users.findAll().stream()
                .anyMatch(user -> PhoneNumbers.normalizeIfPhoneNumber(user.getUsername())
                        .filter(phone::equals).isPresent());
        if (usernameAlreadyUsesPhone || drivers.existsByPhone(phone)) {
            throw new IllegalArgumentException("A user or driver with this mobile number is already registered");
        }
        if (drivers.existsByBusId(request.busId())) {
            throw new IllegalArgumentException("This bus is already assigned to a driver");
        }
        Bus bus = buses.findById(request.busId())
                .orElseThrow(() -> new ResourceNotFoundException("Bus not found"));

        User user = new User();
        user.setUsername(phone);
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole("DRIVER");
        users.save(user);

        Driver driver = new Driver();
        driver.setName(request.name().trim());
        driver.setPhone(phone);
        driver.setUser(user);
        driver.setBus(bus);
        return toResponse(drivers.save(driver));
    }

    @Transactional
    public void delete(Long id) {
        Driver driver = drivers.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found"));
        drivers.delete(driver);
        drivers.flush();
        users.delete(driver.getUser());
    }

    private DriverResponse toResponse(Driver driver) {
        return new DriverResponse(driver.getId(), driver.getName(), driver.getPhone(),
                driver.getBus().getId(), driver.getBus().getBusNumber());
    }
}
