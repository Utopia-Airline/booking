package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;

@Entity
@Table(name = "booking_guest")
public class BookingGuest {
  @Id
  @Column(name = "booking_id")
  private Long bookingId;
  @Column(name = "contact_email", nullable = false)
  String contactEmail;
  @Column(name = "contact_phone", nullable = false)
  String contactPhone;

//  @JsonBackReference
//  @OneToOne(mappedBy = "guest", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
//  private Booking booking;
//  @JsonBackReference
//  @OneToOne(fetch = FetchType.LAZY)
//  @MapsId
//  @JoinColumn(name = "booking_id")
//  private Booking booking;


  public BookingGuest() {
  }

//  public Booking getBooking() {
//    return booking;
//  }
//
//  public void setBooking(Booking booking) {
//    this.booking = booking;
//  }

  public Long getId() {
    return bookingId;
  }

  public void setId(Long bookingId) {
    this.bookingId = bookingId;
  }

  public String getContactEmail() {
    return contactEmail;
  }

  public void setContactEmail(String contactEmail) {
    this.contactEmail = contactEmail;
  }

  public String getContactPhone() {
    return contactPhone;
  }

  public void setContactPhone(String contactPhone) {
    this.contactPhone = contactPhone;
  }
}
