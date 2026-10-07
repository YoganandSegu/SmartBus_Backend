package com.smartbus.controller;

import com.smartbus.dto.BusLocationResponse;
import com.smartbus.dto.LocationUpdateRequest;
import com.smartbus.service.BusTrackingService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tracking")
public class BusTrackingController {
    private final BusTrackingService trackingService;

    public BusTrackingController(BusTrackingService trackingService) {
        this.trackingService = trackingService;
    }

    @GetMapping("/buses/{busNumber}")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    public BusLocationResponse findByBusNumber(@PathVariable String busNumber) {
        return trackingService.findByBusNumber(busNumber);
    }

    @GetMapping("/drivers/{phone}")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    public BusLocationResponse findByDriverPhone(@PathVariable String phone) {
        return trackingService.findByDriverPhone(phone);
    }

    @PutMapping("/buses/{busId}/location")
    @PreAuthorize("hasRole('ADMIN')")
    public BusLocationResponse update(@PathVariable Long busId,
                                      @Valid @RequestBody LocationUpdateRequest request) {
        return trackingService.update(busId, request);
    }

    @PutMapping("/driver/location")
    @PreAuthorize("hasRole('DRIVER')")
    public BusLocationResponse updateForDriver(Authentication authentication,
                                               @Valid @RequestBody LocationUpdateRequest request) {
        return trackingService.updateForDriver(authentication.getName(), request);
    }
}
