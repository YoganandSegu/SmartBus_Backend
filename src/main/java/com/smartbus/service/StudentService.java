package com.smartbus.service;

import com.smartbus.dto.StudentResponse;
import com.smartbus.entity.Student;
import com.smartbus.exception.ResourceNotFoundException;
import com.smartbus.exception.StudentNotFoundException;
import com.smartbus.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentService {
    private final StudentRepository students;

    public StudentService(StudentRepository students) {
        this.students = students;
    }

    public List<StudentResponse> findAll() {
        return students.findAll().stream().map(this::toResponse).toList();
    }

    public StudentResponse findById(Long id) {
        Student student = students.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        return toResponse(student);
    }

    public StudentResponse findProfile(String username) {
        Student student = students.findByUserUsername(username)
                .orElseThrow(() -> new StudentNotFoundException("Student profile was not found"));
        return toResponse(student);
    }

    private StudentResponse toResponse(Student student) {
        return new StudentResponse(student.getId(), student.getStudentId(), student.getName(),
                student.getEmail(), student.getPhone(), student.getDepartment(), student.getYear());
    }
}
