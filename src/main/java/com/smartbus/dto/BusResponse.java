package com.smartbus.dto;

public record BusResponse(
        Long id, String busNumber, String registrationNumber, Integer capacity,
        Long routeId, String routeName, String status, long bookedSeats, long availableSeats) {}
