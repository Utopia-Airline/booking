package com.example.demo.entity;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "flight")
public class Flight {
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "reserved_seats", nullable = false)
  private int reservedSeats;
  @Column(name = "seat_price", nullable = false)
  private float seatPrice;

  @Column(name = "departure_time", nullable = false)
  private Date departureTime;

  @ManyToOne
  @JoinColumn(name = "route_id", nullable = false)
  private Route route;

  public Flight() {
  }

  public Route getRoute() {
    return route;
  }

  public void setRoute(Route route) {
    this.route = route;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public int getReservedSeats() {
    return reservedSeats;
  }

  public void setReservedSeats(int reservedSeats) {
    this.reservedSeats = reservedSeats;
  }

  public float getSeatPrice() {
    return seatPrice;
  }

  public void setSeatPrice(float seatPrice) {
    this.seatPrice = seatPrice;
  }

  public Date getDepartureTime() {
    return departureTime;
  }

  public void setDepartureTime(Date departureTime) {
    this.departureTime = departureTime;
  }
}
