package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "passenger")
public class Passenger {
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "given_name", nullable = false)
  private String givenName;
  @Column(name = "family_name", nullable = false)
  private String familyName;
  @Column(name = "gender", nullable = false)
  private String gender;
  @Column(name = "address", nullable = false)
  private String address;
  @Column(name = "dob", nullable = false)
  private Date dob;

  // you define the column that is being used as foreign key just here
  @JsonBackReference
  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  @JoinColumn(name = "booking_id", nullable = false)
  private Booking booking;

  public Passenger() {
  }

  public Long getId() {
    return id;
  }

  public Booking getBooking() {
    return booking;
  }

  public void setBooking(Booking booking) {
    this.booking = booking;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getGivenName() {
    return givenName;
  }

  public void setGivenName(String givenName) {
    this.givenName = givenName;
  }

  public String getFamilyName() {
    return familyName;
  }

  public void setFamilyName(String familyName) {
    this.familyName = familyName;
  }

  public String getGender() {
    return gender;
  }

  public void setGender(String gender) {
    this.gender = gender;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public Date getDob() {
    return dob;
  }

  public void setDob(String dob) {
    this.dob = Date.valueOf(dob);
  }

  public void updateBy(Passenger newPassenger) {
    if (newPassenger.getGivenName() != null)
      setGivenName(newPassenger.getGivenName());
    if (newPassenger.getFamilyName() != null)
      setFamilyName(newPassenger.getFamilyName());
    if (newPassenger.getGender() != null)
      setGender(newPassenger.getGender());
    if (newPassenger.getAddress() != null)
      setAddress(newPassenger.getAddress());
    if (newPassenger.getDob() != null)
      setDob(newPassenger.getDob().toString());
  }
}
