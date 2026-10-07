package com.smartbus.controller;

import com.smartbus.dto.StudentResponse;
import com.smartbus.service.StudentService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {
    private final StudentService studentService;

    public StudentController(StudentService studentService) { this.studentService = studentService; }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<StudentResponse> findAll() { return studentService.findAll(); }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public StudentResponse findById(@PathVariable Long id) { return studentService.findById(id); }

    @GetMapping("/profile")
    public StudentResponse profile(Authentication authentication) {
        return studentService.findProfile(authentication.getName());
    }
}
