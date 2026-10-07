package com.smartbus.service;

import com.smartbus.dto.BusRequest;
import com.smartbus.dto.BusResponse;
import com.smartbus.entity.Bus;
import com.smartbus.entity.Route;
import com.smartbus.exception.ResourceNotFoundException;
import com.smartbus.repository.BookingRepository;
import com.smartbus.repository.BusLocationRepository;
import com.smartbus.repository.BusRepository;
import com.smartbus.repository.RouteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BusService {
    private final BusRepository buses;
    private final RouteRepository routes;
    private final BookingRepository bookings;
    private final BusLocationRepository locations;

    public BusService(BusRepository buses, RouteRepository routes, BookingRepository bookings,
                      BusLocationRepository locations) {
        this.buses = buses;
        this.routes = routes;
        this.bookings = bookings;
        this.locations = locations;
    }

    public List<BusResponse> findAll() {
        return buses.findAll().stream().map(this::toResponse).toList();
    }

    public BusResponse findById(Long id) {
        return toResponse(getBus(id));
    }

    public BusResponse create(BusRequest request) {
        validateUnique(request, null);
        Bus bus = new Bus();
        apply(bus, request);
        return toResponse(buses.save(bus));
    }

    @Transactional
    public BusResponse update(Long id, BusRequest request) {
        Bus bus = getBus(id);
        validateUnique(request, id);
        long occupiedSeats = bookings.countByBusIdAndStatus(id, "BOOKED");
        if (request.capacity() < occupiedSeats) {
            throw new IllegalArgumentException("Capacity cannot be lower than the number of booked seats");
        }
        apply(bus, request);
        return toResponse(buses.save(bus));
    }

    @Transactional
    public void delete(Long id) {
        locations.deleteByBusId(id);
        buses.delete(getBus(id));
    }

    private Bus getBus(Long id) {
        return buses.findById(id).orElseThrow(() -> new ResourceNotFoundException("Bus not found"));
    }

    private void validateUnique(BusRequest request, Long currentId) {
        buses.findAll().stream()
                .filter(existing -> !existing.getId().equals(currentId))
                .filter(existing -> existing.getBusNumber().equalsIgnoreCase(request.busNumber().trim())
                        || existing.getRegistrationNumber().equalsIgnoreCase(request.registrationNumber().trim()))
                .findFirst().ifPresent(existing -> {
                    throw new IllegalArgumentException("Bus number or registration number is already in use");
                });
        if (!request.status().equals("ACTIVE") && !request.status().equals("INACTIVE")) {
            throw new IllegalArgumentException("Status must be ACTIVE or INACTIVE");
        }
    }

    private void apply(Bus bus, BusRequest request) {
        Route route = routes.findById(request.routeId())
                .orElseThrow(() -> new ResourceNotFoundException("Route not found"));
        bus.setBusNumber(request.busNumber().trim());
        bus.setRegistrationNumber(request.registrationNumber().trim());
        bus.setCapacity(request.capacity());
        bus.setRoute(route);
        bus.setStatus(request.status());
    }

    private BusResponse toResponse(Bus bus) {
        long booked = bookings.countByBusIdAndStatus(bus.getId(), "BOOKED");
        return new BusResponse(bus.getId(), bus.getBusNumber(), bus.getRegistrationNumber(),
                bus.getCapacity(), bus.getRoute().getId(), bus.getRoute().getRouteName(),
                bus.getStatus(), booked, bus.getCapacity() - booked);
    }
}
