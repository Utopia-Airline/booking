package com.example.demo.entity;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "user_booking")
public class UserBooking {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;
  @Column(name = "is_active", nullable = false)
  boolean isActive;
  @Column(name = "confirmation_code", nullable = false)
  String confirmationCode;

  @OneToMany(mappedBy = "booking", fetch = FetchType.EAGER)
  private List<Passenger> passengers;

  @ManyToMany
  @JoinTable(
    name = "flight_bookings",
    joinColumns = @JoinColumn(name = "booking_id"),
    inverseJoinColumns = @JoinColumn(name = "flight_id"))
  private Set<Flight> flights;

  @ManyToOne
  @JoinTable(
    name = "booking_user",
    joinColumns = @JoinColumn(name = "booking_id"),
    inverseJoinColumns = @JoinColumn(name = "user_id"))
  private User user;

  @ManyToOne
  @JoinTable(
    name = "booking_agent",
    joinColumns = @JoinColumn(name = "booking_id"),
    inverseJoinColumns = @JoinColumn(name = "agent_id"))
  private User agent;

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  @Transient
  private String type = "USER";

  public User getAgent() {
    return agent;
  }

  public void setAgent(User agent) {
    this.agent = agent;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public Set<Flight> getFlights() {
    return flights;
  }

  public void setFlights(Set<Flight> flights) {
    this.flights = flights;
  }

  public List<Passenger> getPassengers() {
    return passengers;
  }

  public void setPassengers(List<Passenger> passengers) {
    this.passengers = passengers;
  }

  public UserBooking() {
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public boolean getIsActive() {
    return isActive;
  }

  public void setIsActive(boolean active) {
    isActive = active;
  }

  public String getConfirmationCode() {
    return confirmationCode;
  }

  public void setConfirmationCode(String confirmationCode) {
    this.confirmationCode = confirmationCode;
  }
}
