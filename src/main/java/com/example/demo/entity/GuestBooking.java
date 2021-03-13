package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "GUEST_BOOKING")
public class GuestBooking {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;
  @Column(name = "is_active", nullable = false)
  boolean isActive;
  @Column(name = "confirmation_code", nullable = false)
  String confirmationCode;

  @OneToMany(mappedBy = "booking", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  private List<Passenger> passengers;

  @JsonProperty(value = "guest")
  @OneToOne(fetch = FetchType.EAGER, optional = false)
  @JoinColumn(name = "id", referencedColumnName = "booking_id")
  private BookingGuest guest;

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  @Transient
  private String type = "GUEST";

  public BookingGuest getGuest() {
    return guest;
  }

  public void setGuest(BookingGuest guest) {
    this.guest = guest;
  }

  public List<Passenger> getPassengers() {
    return passengers;
  }

  public void setPassengers(List<Passenger> passengers) {
    this.passengers = passengers;
  }

  @ManyToMany
  @JoinTable(
    name = "flight_bookings",
    joinColumns = @JoinColumn(name = "booking_id"),
    inverseJoinColumns = @JoinColumn(name = "flight_id"))
  private Set<Flight> flights;


  public Set<Flight> getFlights() {
    return flights;
  }

  public void setFlights(Set<Flight> flights) {
    this.flights = flights;
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
