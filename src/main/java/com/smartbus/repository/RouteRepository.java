package com.smartbus.repository;

import com.smartbus.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RouteRepository extends JpaRepository<Route, Long> {
    boolean existsByRouteName(String routeName);
}
