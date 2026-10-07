package com.smartbus.service;

import com.smartbus.dto.RouteRequest;
import com.smartbus.entity.Route;
import com.smartbus.exception.ResourceNotFoundException;
import com.smartbus.repository.RouteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RouteService {
    private final RouteRepository routes;

    public RouteService(RouteRepository routes) {
        this.routes = routes;
    }

    public List<Route> findAll() { return routes.findAll(); }

    public Route findById(Long id) {
        return routes.findById(id).orElseThrow(() -> new ResourceNotFoundException("Route not found"));
    }

    public Route create(RouteRequest request) {
        if (routes.existsByRouteName(request.routeName().trim())) {
            throw new IllegalArgumentException("Route name is already in use");
        }
        Route route = new Route();
        apply(route, request);
        return routes.save(route);
    }

    public Route update(Long id, RouteRequest request) {
        Route route = findById(id);
        if (!route.getRouteName().equalsIgnoreCase(request.routeName().trim())
                && routes.existsByRouteName(request.routeName().trim())) {
            throw new IllegalArgumentException("Route name is already in use");
        }
        apply(route, request);
        return routes.save(route);
    }

    public void delete(Long id) {
        routes.delete(findById(id));
    }

    private void apply(Route route, RouteRequest request) {
        route.setRouteName(request.routeName().trim());
        route.setStartPoint(request.startPoint().trim());
        route.setDestination(request.destination().trim());
        route.setStops(request.stops().trim());
    }
}
