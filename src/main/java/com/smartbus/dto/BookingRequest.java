package com.smartbus.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record BookingRequest(
        @NotBlank String studentId,
        @NotNull Long busId,
        @NotNull Long routeId,
        @NotNull @Positive Integer seatNumber) {}
