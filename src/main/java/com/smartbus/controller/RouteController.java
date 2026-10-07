package com.smartbus.controller;

import com.smartbus.dto.RouteRequest;
import com.smartbus.entity.Route;
import com.smartbus.service.RouteService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/routes")
public class RouteController {
    private final RouteService routeService;

    public RouteController(RouteService routeService) { this.routeService = routeService; }

    @GetMapping
    public List<Route> findAll() { return routeService.findAll(); }

    @GetMapping("/{id}")
    public Route findById(@PathVariable Long id) { return routeService.findById(id); }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Route create(@Valid @RequestBody RouteRequest request) { return routeService.create(request); }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Route update(@PathVariable Long id, @Valid @RequestBody RouteRequest request) {
        return routeService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) { routeService.delete(id); }
}
