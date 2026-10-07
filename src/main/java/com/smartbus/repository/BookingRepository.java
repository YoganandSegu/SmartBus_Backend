package com.smartbus.repository;

import com.smartbus.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    boolean existsByBusIdAndSeatNumberAndStatus(Long busId, Integer seatNumber, String status);
    boolean existsByStudentStudentIdAndStatus(String studentId, String status);
    List<Booking> findByStudentStudentIdOrderByBookingDateDescIdDesc(String studentId);
    long countByBusIdAndStatus(Long busId, String status);
    long countByStatusAndBookingDate(String status, LocalDate bookingDate);
    List<Booking> findAllByOrderByBookingDateDescIdDesc();
}
