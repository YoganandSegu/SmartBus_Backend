package com.smartbus.service;

import com.smartbus.dto.BookingRequest;
import com.smartbus.dto.BookingResponse;
import com.smartbus.entity.Booking;
import com.smartbus.entity.Bus;
import com.smartbus.entity.Route;
import com.smartbus.entity.Student;
import com.smartbus.exception.BusFullException;
import com.smartbus.exception.DuplicateBookingException;
import com.smartbus.exception.ResourceNotFoundException;
import com.smartbus.exception.StudentNotFoundException;
import com.smartbus.repository.BookingRepository;
import com.smartbus.repository.BusRepository;
import com.smartbus.repository.RouteRepository;
import com.smartbus.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

@Service
public class BookingService {
    private final BookingRepository bookings;
    private final StudentRepository students;
    private final BusRepository buses;
    private final RouteRepository routes;

    public BookingService(BookingRepository bookings, StudentRepository students,
                         BusRepository buses, RouteRepository routes) {
        this.bookings = bookings;
        this.students = students;
        this.buses = buses;
        this.routes = routes;
    }

    @Transactional
    public BookingResponse create(BookingRequest request) {
        Student student = students.findByStudentIdForUpdate(request.studentId().trim().toUpperCase(Locale.ROOT))
                .orElseThrow(() -> new StudentNotFoundException("Student ID was not found"));
        Bus bus = buses.findByIdForUpdate(request.busId())
                .orElseThrow(() -> new ResourceNotFoundException("Bus not found"));
        Route route = routes.findById(request.routeId())
                .orElseThrow(() -> new ResourceNotFoundException("Route not found"));

        if (!"ACTIVE".equals(bus.getStatus())) {
            throw new IllegalArgumentException("This bus is not currently active");
        }
        if (!bus.getRoute().getId().equals(route.getId())) {
            throw new IllegalArgumentException("Selected route does not belong to this bus");
        }
        if (request.seatNumber() > bus.getCapacity()) {
            throw new IllegalArgumentException("Seat number must be between 1 and " + bus.getCapacity());
        }
        if (bookings.existsByBusIdAndSeatNumberAndStatus(bus.getId(), request.seatNumber(), "BOOKED")) {
            throw new DuplicateBookingException("Seat is already booked");
        }
        if (bookings.existsByStudentStudentIdAndStatus(student.getStudentId(), "BOOKED")) {
            throw new DuplicateBookingException("This student already has an active booking");
        }
        long bookedSeats = bookings.countByBusIdAndStatus(bus.getId(), "BOOKED");
        if (bookedSeats >= bus.getCapacity()) {
            throw new BusFullException("This bus is full");
        }

        Booking booking = new Booking();
        booking.setStudent(student);
        booking.setBus(bus);
        booking.setRoute(route);
        booking.setSeatNumber(request.seatNumber());
        booking.setBookingDate(LocalDate.now());
        booking.setStatus("BOOKED");
        return toResponse(bookings.save(booking));
    }

    public List<BookingResponse> findByStudentId(String studentId) {
        String normalizedStudentId = studentId.trim().toUpperCase(Locale.ROOT);
        if (!students.existsByStudentId(normalizedStudentId)) {
            throw new StudentNotFoundException("Student ID was not found");
        }
        return bookings.findByStudentStudentIdOrderByBookingDateDescIdDesc(normalizedStudentId)
                .stream().map(this::toResponse).toList();
    }

    public List<BookingResponse> findAll() {
        return bookings.findAllByOrderByBookingDateDescIdDesc().stream().map(this::toResponse).toList();
    }

    @Transactional
    public void cancel(Long bookingId) {
        Booking booking = bookings.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        if (!"BOOKED".equals(booking.getStatus())) {
            throw new IllegalArgumentException("Booking has already been cancelled");
        }
        booking.setStatus("CANCELLED");
    }

    private BookingResponse toResponse(Booking booking) {
        return new BookingResponse(booking.getId(), booking.getStudent().getStudentId(),
                booking.getStudent().getName(), booking.getBus().getId(), booking.getBus().getBusNumber(),
                booking.getRoute().getId(), booking.getRoute().getRouteName(), booking.getSeatNumber(),
                booking.getBookingDate(), booking.getStatus());
    }
}
