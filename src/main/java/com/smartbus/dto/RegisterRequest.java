package com.smartbus.dto;

import jakarta.validation.constraints.*;

public record RegisterRequest(
        @NotBlank @Pattern(regexp = "(?i)[0-9]{2}[a-z]{2}[0-9]{3}[a-z][0-9]{2}",
                message = "Student ID must follow YY + department code + 3 digits + section + 2 digits (example: 24eg110b63)") String studentId,
        @NotBlank @Size(max = 100) String name,
        @NotBlank @Email String email,
        @NotBlank @Pattern(regexp = "[0-9+() -]{7,20}") String phone,
        @NotBlank @Size(max = 100) String department,
        @NotNull @Min(1) @Max(8) Integer year,
        @NotBlank @Size(min = 3, max = 50) String username,
        @NotBlank @Size(min = 8, max = 72) String password) {}
