package com.smartbus.dto;

public record LoginResponse(boolean success, String username, String role, String studentId, String name,
                            Long assignedBusId, String assignedBusNumber) {}
