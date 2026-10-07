package com.smartbus.service;

import com.smartbus.dto.LoginRequest;
import com.smartbus.dto.LoginResponse;
import com.smartbus.dto.RegisterRequest;
import com.smartbus.entity.Student;
import com.smartbus.entity.User;
import com.smartbus.exception.StudentNotFoundException;
import com.smartbus.repository.DriverRepository;
import com.smartbus.repository.StudentRepository;
import com.smartbus.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository users;
    private final StudentRepository students;
    private final DriverRepository drivers;
    private final PasswordEncoder passwordEncoder;

    public AuthService(AuthenticationManager authenticationManager, UserRepository users,
                       StudentRepository students, DriverRepository drivers, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.users = users;
        this.students = students;
        this.drivers = drivers;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        User user = users.findByUsername(authentication.getName())
                .orElseThrow(() -> new StudentNotFoundException("Account was not found"));
        if ("STUDENT".equals(user.getRole())) {
            Student student = students.findByUserUsername(user.getUsername())
                    .orElseThrow(() -> new StudentNotFoundException("Student profile was not found"));
            return new LoginResponse(true, user.getUsername(), user.getRole(), student.getStudentId(),
                    student.getName(), null, null);
        }
        if ("DRIVER".equals(user.getRole())) {
            var driver = drivers.findByUserUsername(user.getUsername())
                    .orElseThrow(() -> new StudentNotFoundException("Driver profile was not found"));
            return new LoginResponse(true, user.getUsername(), user.getRole(), null, driver.getName(),
                    driver.getBus().getId(), driver.getBus().getBusNumber());
        }
        return new LoginResponse(true, user.getUsername(), user.getRole(), null, null, null, null);
    }

    @Transactional
    public LoginResponse register(RegisterRequest request) {
        String studentId = request.studentId().trim().toUpperCase(Locale.ROOT);
        if (users.existsByUsername(request.username())) {
            throw new IllegalArgumentException("Username is already registered");
        }
        if (students.existsByStudentId(studentId)) {
            throw new IllegalArgumentException("Student ID is already registered");
        }
        if (students.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email is already registered");
        }

        User user = new User();
        user.setUsername(request.username().trim());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole("STUDENT");
        users.save(user);

        Student student = new Student();
        student.setStudentId(studentId);
        student.setName(request.name().trim());
        student.setEmail(request.email().trim());
        student.setPhone(request.phone().trim());
        student.setDepartment(request.department().trim());
        student.setYear(request.year());
        student.setUser(user);
        students.save(student);
        return new LoginResponse(true, user.getUsername(), user.getRole(), student.getStudentId(),
                student.getName(), null, null);
    }
}
