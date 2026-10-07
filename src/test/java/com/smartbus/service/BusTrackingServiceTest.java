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
import org.springframework.test.util.ReflectionTestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BusTrackingServiceTest {
    @Mock
    private BusRepository buses;
    @Mock
    private BusLocationRepository locations;
    @Mock
    private DriverRepository drivers;
    @InjectMocks
    private BusTrackingService service;

    @Test
    void updateStoresLatestCoordinatesAndTimestamp() {
        Bus bus = new Bus();
        bus.setBusNumber("BUS-101");
        ReflectionTestUtils.setField(bus, "id", 12L);
        when(buses.findByIdForUpdate(12L)).thenReturn(Optional.of(bus));
        when(locations.findByBusId(12L)).thenReturn(Optional.empty());
        when(locations.save(any(BusLocation.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Instant timestamp = Instant.parse("2026-10-07T06:30:00Z");

        BusLocationResponse result = service.update(12L, new LocationUpdateRequest(17.3850, 78.4867, timestamp));

        ArgumentCaptor<BusLocation> saved = ArgumentCaptor.forClass(BusLocation.class);
        verify(locations).save(saved.capture());
        assertSame(bus, saved.getValue().getBus());
        assertEquals(17.3850, saved.getValue().getLatitude());
        assertEquals(78.4867, saved.getValue().getLongitude());
        assertEquals(timestamp, saved.getValue().getTimestamp());
        assertEquals("BUS-101", result.busNumber());
        assertEquals(timestamp, result.timestamp());
    }

    @Test
    void updateDoesNotReplaceLocationWithAnOlderReading() {
        Bus bus = new Bus();
        bus.setBusNumber("BUS-101");
        ReflectionTestUtils.setField(bus, "id", 12L);
        Instant latestTimestamp = Instant.parse("2026-10-07T06:31:00Z");
        BusLocation current = new BusLocation();
        current.setBus(bus);
        current.setLatitude(17.3850);
        current.setLongitude(78.4867);
        current.setTimestamp(latestTimestamp);
        when(buses.findByIdForUpdate(12L)).thenReturn(Optional.of(bus));
        when(locations.findByBusId(12L)).thenReturn(Optional.of(current));

        BusLocationResponse result = service.update(12L,
                new LocationUpdateRequest(17.4000, 78.5000, latestTimestamp.minusSeconds(10)));

        assertEquals(17.3850, result.latitude());
        assertEquals(78.4867, result.longitude());
        assertEquals(latestTimestamp, result.timestamp());
        verify(locations, never()).save(any(BusLocation.class));
    }

    @Test
    void findByBusNumberReturnsLatestReportedPositionCaseInsensitively() {
        Bus bus = new Bus();
        bus.setBusNumber("BUS-101");
        BusLocation location = new BusLocation();
        location.setBus(bus);
        location.setLatitude(17.3850);
        location.setLongitude(78.4867);
        location.setTimestamp(Instant.parse("2026-10-07T06:30:00Z"));
        when(locations.findByBusBusNumberIgnoreCase("bus-101")).thenReturn(Optional.of(location));

        BusLocationResponse result = service.findByBusNumber("bus-101");

        assertEquals("BUS-101", result.busNumber());
        assertEquals(17.3850, result.latitude());
        assertEquals(78.4867, result.longitude());
    }

    @Test
    void findByBusNumberReportsWhenNoLocationHasBeenReceived() {
        when(locations.findByBusBusNumberIgnoreCase("BUS-101")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findByBusNumber("BUS-101"));
    }

    @Test
    void updateForDriverOnlyUpdatesTheAssignedBus() {
        Bus bus = new Bus();
        bus.setBusNumber("BUS-202");
        ReflectionTestUtils.setField(bus, "id", 22L);
        Driver driver = new Driver();
        driver.setBus(bus);
        when(drivers.findByUserUsername("+15551234567")).thenReturn(Optional.of(driver));
        when(buses.findByIdForUpdate(22L)).thenReturn(Optional.of(bus));
        when(locations.findByBusId(22L)).thenReturn(Optional.empty());
        when(locations.save(any(BusLocation.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Instant timestamp = Instant.parse("2026-10-07T06:30:00Z");

        BusLocationResponse result = service.updateForDriver("+15551234567",
                new LocationUpdateRequest(17.3850, 78.4867, timestamp));

        assertEquals("BUS-202", result.busNumber());
        verify(buses).findByIdForUpdate(22L);
        verify(locations).findByBusId(22L);
    }

    @Test
    void findByDriverPhoneReturnsLocationForTheAssignedBus() {
        Bus bus = new Bus();
        bus.setBusNumber("BUS-202");
        ReflectionTestUtils.setField(bus, "id", 22L);
        Driver driver = new Driver();
        driver.setBus(bus);
        BusLocation location = new BusLocation();
        location.setBus(bus);
        location.setLatitude(17.3850);
        location.setLongitude(78.4867);
        Instant timestamp = Instant.parse("2026-10-07T06:30:00Z");
        location.setTimestamp(timestamp);
        when(drivers.findByPhone("+15551234567")).thenReturn(Optional.of(driver));
        when(locations.findByBusId(22L)).thenReturn(Optional.of(location));

        BusLocationResponse result = service.findByDriverPhone("+1 (555) 123-4567");

        assertEquals("BUS-202", result.busNumber());
        assertEquals(17.3850, result.latitude());
        assertEquals(timestamp, result.timestamp());
        verify(locations).findByBusId(22L);
    }
}
