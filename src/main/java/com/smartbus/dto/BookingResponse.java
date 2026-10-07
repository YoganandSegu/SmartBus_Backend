package com.smartbus.dto;

import java.time.LocalDate;

public record BookingResponse(
        Long id, String studentId, String studentName, Long busId, String busNumber,
        Long routeId, String routeName, Integer seatNumber, LocalDate bookingDate, String status) {}
