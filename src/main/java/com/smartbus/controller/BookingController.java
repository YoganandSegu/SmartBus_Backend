package com.smartbus.controller;

import com.smartbus.dto.ApiResponse;
import com.smartbus.dto.BookingRequest;
import com.smartbus.dto.BookingResponse;
import com.smartbus.service.BookingService;
import com.smartbus.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    private final BookingService bookingService;
    private final StudentService studentService;

    public BookingController(BookingService bookingService, StudentService studentService) {
        this.bookingService = bookingService;
        this.studentService = studentService;
    }

    @PostMapping
    public BookingResponse create(@Valid @RequestBody BookingRequest request, Authentication authentication) {
        if (authentication.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ROLE_STUDENT"))) {
            String ownStudentId = studentService.findProfile(authentication.getName()).studentId();
            if (!ownStudentId.equalsIgnoreCase(request.studentId().trim())) {
                throw new AccessDeniedException("Students can only book for their own Student ID");
            }
        }
        return bookingService.create(request);
    }

    @GetMapping("/student/{studentId}")
    public List<BookingResponse> findByStudentId(@PathVariable String studentId, Authentication authentication) {
        String ownStudentId = studentService.findProfile(authentication.getName()).studentId();
        if (!ownStudentId.equalsIgnoreCase(studentId.trim())) {
            throw new AccessDeniedException("Students can only view their own bookings");
        }
        return bookingService.findByStudentId(studentId);
    }

    @GetMapping
    public List<BookingResponse> findAll(Authentication authentication) {
        if (authentication.getAuthorities().stream().noneMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"))) {
            throw new AccessDeniedException("Only admins can view all bookings");
        }
        return bookingService.findAll();
    }

    @DeleteMapping("/{id}")
    public ApiResponse cancel(@PathVariable Long id, Authentication authentication) {
        if (authentication.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ROLE_STUDENT"))) {
            String ownStudentId = studentService.findProfile(authentication.getName()).studentId();
            boolean ownsBooking = bookingService.findByStudentId(ownStudentId).stream()
                    .anyMatch(booking -> booking.id().equals(id));
            if (!ownsBooking) {
                throw new AccessDeniedException("Students can only cancel their own bookings");
            }
        }
        bookingService.cancel(id);
        return new ApiResponse(true, "Booking cancelled successfully");
    }
}
