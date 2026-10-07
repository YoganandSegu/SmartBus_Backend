package com.smartbus.service;

import com.smartbus.dto.DriverRequest;
import com.smartbus.dto.DriverResponse;
import com.smartbus.entity.Bus;
import com.smartbus.entity.Driver;
import com.smartbus.entity.User;
import com.smartbus.repository.BusRepository;
import com.smartbus.repository.DriverRepository;
import com.smartbus.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DriverServiceTest {
    @Mock
    private DriverRepository drivers;
    @Mock
    private UserRepository users;
    @Mock
    private BusRepository buses;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private DriverService service;

    @Test
    void createNormalizesPhoneAndCreatesDriverBoundToOneBus() {
        Bus bus = new Bus();
        bus.setBusNumber("BUS-101");
        ReflectionTestUtils.setField(bus, "id", 12L);
        when(users.findAll()).thenReturn(List.of());
        when(drivers.existsByPhone("+15551234567")).thenReturn(false);
        when(drivers.existsByBusId(12L)).thenReturn(false);
        when(buses.findById(12L)).thenReturn(Optional.of(bus));
        when(passwordEncoder.encode("initial123")).thenReturn("hashed-password");
        when(users.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(drivers.save(any(Driver.class))).thenAnswer(invocation -> invocation.getArgument(0));

        DriverResponse result = service.create(new DriverRequest(
                "Alex Driver", "+1 (555) 123-4567", "initial123", 12L));

        ArgumentCaptor<User> savedUser = ArgumentCaptor.forClass(User.class);
        verify(users).save(savedUser.capture());
        assertEquals("+15551234567", savedUser.getValue().getUsername());
        assertEquals("DRIVER", savedUser.getValue().getRole());
        assertEquals("hashed-password", savedUser.getValue().getPassword());

        ArgumentCaptor<Driver> savedDriver = ArgumentCaptor.forClass(Driver.class);
        verify(drivers).save(savedDriver.capture());
        assertEquals("+15551234567", savedDriver.getValue().getPhone());
        assertSame(bus, savedDriver.getValue().getBus());
        assertEquals("BUS-101", result.busNumber());
    }

    @Test
    void createRejectsPhoneAlreadyUsedByExistingAccount() {
        User existingUser = new User();
        existingUser.setUsername("+1 (555) 123-4567");
        when(users.findAll()).thenReturn(List.of(existingUser));

        assertThrows(IllegalArgumentException.class, () ->
                service.create(new DriverRequest("Alex Driver", "+15551234567", "initial123", 12L)));

        verify(drivers, never()).save(any(Driver.class));
        verify(users, never()).save(any(User.class));
    }
}
