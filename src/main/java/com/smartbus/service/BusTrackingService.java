package com.smartbus.service;

import com.smartbus.dto.BusLocationResponse;
import com.smartbus.dto.LocationUpdateRequest;
import com.smartbus.entity.Bus;
import com.smartbus.entity.BusLocation;
import com.smartbus.entity.Driver;
import com.smartbus.exception.ResourceNotFoundException;
import com.smartbus.repository.BusLocationRepository;
import com.smartbus.repository.BusRepository;
import com.smartbus.repository.DriverRepository;
import com.smartbus.util.PhoneNumbers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BusTrackingService {
    private final BusRepository buses;
    private final BusLocationRepository locations;
    private final DriverRepository drivers;

    public BusTrackingService(BusRepository buses, BusLocationRepository locations, DriverRepository drivers) {
        this.buses = buses;
        this.locations = locations;
        this.drivers = drivers;
    }

    @Transactional
    public BusLocationResponse update(Long busId, LocationUpdateRequest request) {
        Bus bus = buses.findByIdForUpdate(busId)
                .orElseThrow(() -> new ResourceNotFoundException("Bus not found"));
        return update(bus, request);
    }

    @Transactional
    public BusLocationResponse updateForDriver(String username, LocationUpdateRequest request) {
        Driver driver = drivers.findByUserUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Driver profile was not found"));
        Bus bus = buses.findByIdForUpdate(driver.getBus().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Assigned bus was not found"));
        return update(bus, request);
    }

    private BusLocationResponse update(Bus bus, LocationUpdateRequest request) {
        Long busId = bus.getId();
        BusLocation location = locations.findByBusId(busId).orElseGet(BusLocation::new);
        if (location.getTimestamp() != null && location.getTimestamp().isAfter(request.timestamp())) {
            return toResponse(location);
        }
        location.setBus(bus);
        location.setLatitude(request.latitude());
        location.setLongitude(request.longitude());
        location.setTimestamp(request.timestamp());
        return toResponse(locations.save(location));
    }

    @Transactional(readOnly = true)
    public BusLocationResponse findByBusNumber(String busNumber) {
        BusLocation location = locations.findByBusBusNumberIgnoreCase(busNumber.trim())
                .orElseThrow(() -> new ResourceNotFoundException("No location has been reported for this bus yet"));
        return toResponse(location);
    }

    @Transactional(readOnly = true)
    public BusLocationResponse findByDriverPhone(String phone) {
        Driver driver = drivers.findByPhone(PhoneNumbers.normalize(phone))
                .orElseThrow(() -> new ResourceNotFoundException("No driver is registered with this mobile number"));
        BusLocation location = locations.findByBusId(driver.getBus().getId())
                .orElseThrow(() -> new ResourceNotFoundException("No location has been reported for this driver's bus yet"));
        return toResponse(location);
    }

    private BusLocationResponse toResponse(BusLocation location) {
        return new BusLocationResponse(location.getBus().getId(), location.getBus().getBusNumber(),
                location.getLatitude(), location.getLongitude(), location.getTimestamp());
    }
}
