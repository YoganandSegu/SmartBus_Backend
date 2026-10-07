package com.smartbus.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RouteRequest(
        @NotBlank @Size(max = 100) String routeName,
        @NotBlank @Size(max = 100) String startPoint,
        @NotBlank @Size(max = 100) String destination,
        @NotBlank @Size(max = 2000) String stops) {}
