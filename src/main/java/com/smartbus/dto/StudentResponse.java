package com.smartbus.dto;

public record StudentResponse(
        Long id, String studentId, String name, String email,
        String phone, String department, Integer year) {}
