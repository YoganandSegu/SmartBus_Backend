package com.smartbus.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "routes")
public class Route {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String routeName;
    @Column(nullable = false)
    private String startPoint;
    @Column(nullable = false)
    private String destination;
    @Column(nullable = false, length = 2000)
    private String stops;

    public Long getId() { return id; }
    public String getRouteName() { return routeName; }
    public void setRouteName(String routeName) { this.routeName = routeName; }
    public String getStartPoint() { return startPoint; }
    public void setStartPoint(String startPoint) { this.startPoint = startPoint; }
    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }
    public String getStops() { return stops; }
    public void setStops(String stops) { this.stops = stops; }
}
