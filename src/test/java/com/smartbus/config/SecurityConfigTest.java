package com.smartbus.config;

import com.smartbus.entity.User;
import com.smartbus.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class SecurityConfigTest {
    @Test
    void userDetailsServiceAcceptsFormattedDriverMobileNumber() {
        UserRepository users = mock(UserRepository.class);
        User driver = new User();
        driver.setUsername("+15551234567");
        driver.setPassword("encoded");
        driver.setRole("DRIVER");
        when(users.findByUsername("+1 (555) 123-4567")).thenReturn(Optional.empty());
        when(users.findByUsername("+15551234567")).thenReturn(Optional.of(driver));

        UserDetailsService service = new SecurityConfig().userDetailsService(users);

        assertEquals("+15551234567", service.loadUserByUsername("+1 (555) 123-4567").getUsername());
        verify(users).findByUsername("+1 (555) 123-4567");
        verify(users).findByUsername("+15551234567");
    }
}
