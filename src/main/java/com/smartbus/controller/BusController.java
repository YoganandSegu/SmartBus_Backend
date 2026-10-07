package com.smartbus.controller;

import com.smartbus.dto.BusRequest;
import com.smartbus.dto.BusResponse;
import com.smartbus.service.BusService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/buses")
public class BusController {
    private final BusService busService;

    public BusController(BusService busService) { this.busService = busService; }

    @GetMapping
    public List<BusResponse> findAll() { return busService.findAll(); }

    @GetMapping("/{id}")
    public BusResponse findById(@PathVariable Long id) { return busService.findById(id); }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public BusResponse create(@Valid @RequestBody BusRequest request) { return busService.create(request); }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public BusResponse update(@PathVariable Long id, @Valid @RequestBody BusRequest request) {
        return busService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) { busService.delete(id); }
}
