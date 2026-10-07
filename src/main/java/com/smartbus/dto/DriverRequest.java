package com.smartbus.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record DriverRequest(
        @NotBlank @Size(max = 100) String name,
        @NotBlank @Pattern(regexp = "[0-9+() -]{7,25}") String phone,
        @NotBlank @Size(min = 8, max = 72) String password,
        @NotNull Long busId) {}
