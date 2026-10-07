package com.smartbus.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "buses")
public class Bus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String busNumber;
    @Column(nullable = false, unique = true)
    private String registrationNumber;
    @Column(nullable = false)
    private Integer capacity;
    @ManyToOne(optional = false)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;
    @Column(nullable = false)
    private String status;

    public Long getId() { return id; }
    public String getBusNumber() { return busNumber; }
    public void setBusNumber(String busNumber) { this.busNumber = busNumber; }
    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }
    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }
    public Route getRoute() { return route; }
    public void setRoute(Route route) { this.route = route; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
