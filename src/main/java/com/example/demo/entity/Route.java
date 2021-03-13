package com.example.demo.entity;

import javax.persistence.*;

@Entity
@Table(name = "route")
public class Route {
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "origin_id", nullable = false)
  private Airport origin;
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "destination_id", nullable = false)
  private Airport destination;


  public Airport getOrigin() {
    return origin;
  }

  public void setOrigin(Airport origin) {
    this.origin = origin;
  }

  public Airport getDestination() {
    return destination;
  }

  public void setDestination(Airport destination) {
    this.destination = destination;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }
}
