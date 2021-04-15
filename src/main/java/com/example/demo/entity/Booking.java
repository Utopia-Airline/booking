package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;


@Entity
@Table(name = "Booking")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Booking {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;
  @Column(name = "is_active", nullable = false)
  boolean isActive;
  @Column(name = "confirmation_code", nullable = false)
  String confirmationCode;

  @JsonManagedReference
  @OneToMany(mappedBy = "booking", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  private List<Passenger> passengers;

  @ManyToMany
  @JoinTable(
    name = "flight_bookings",
    joinColumns = @JoinColumn(name = "booking_id"),
    inverseJoinColumns = @JoinColumn(name = "flight_id"))
  private List<Flight> flights;

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

  //  @JsonProperty(value = "guest")
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id", referencedColumnName = "booking_id")
  private BookingGuest guest;

//  @OneToOne(fetch = FetchType.LAZY, mappedBy = "booking", cascade = CascadeType.ALL)
//  @PrimaryKeyJoinColumn
//  private BookingGuest guest;

  @Transient
  private String type;

  public BookingGuest getGuest() {
    this.type = (this.guest != null) ? "GUEST" : "USER";
    return guest;
  }

  public void setGuest(BookingGuest guest) {
    this.guest = guest;
  }

  public String getType() {
    return type;
  }


  public User getUser() {
    this.type = (this.guest != null) ? "GUEST" : "USER";
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public User getAgent() {
    return agent;
  }

  public void setAgent(User agent) {
    this.agent = agent;
  }

  public List<Flight> getFlights() {
    return flights;
  }

  public void setFlights(List<Flight> flights) {
    this.flights = flights;
  }

  public List<Passenger> getPassengers() {
    return passengers;
  }

  public void setPassengers(List<Passenger> passengers) {
    this.passengers = passengers;
  }

  public Booking() {
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

  public void setIsActive(boolean isActive) {
    this.isActive = isActive;
  }

  public String getConfirmationCode() {
    return confirmationCode;
  }

  public void setConfirmationCode() {
    this.confirmationCode = UUID.randomUUID().toString();
  }

}
