package com.smartbus.dto;

import java.time.Instant;

public record BusLocationResponse(
        Long busId, String busNumber, Double latitude, Double longitude, Instant timestamp) {}
