package com.smartbus.service;

import com.smartbus.dto.AdminStatsResponse;
import com.smartbus.repository.BookingRepository;
import com.smartbus.repository.BusRepository;
import com.smartbus.repository.RouteRepository;
import com.smartbus.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class AdminService {
    private final StudentRepository students;
    private final BusRepository buses;
    private final RouteRepository routes;
    private final BookingRepository bookings;

    public AdminService(StudentRepository students, BusRepository buses,
                        RouteRepository routes, BookingRepository bookings) {
        this.students = students;
        this.buses = buses;
        this.routes = routes;
        this.bookings = bookings;
    }

    public AdminStatsResponse getStats() {
        return new AdminStatsResponse(students.count(), buses.count(), routes.count(),
                bookings.countByStatusAndBookingDate("BOOKED", LocalDate.now()));
    }
}
