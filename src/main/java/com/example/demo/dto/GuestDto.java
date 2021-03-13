package com.example.demo.dto;

import com.example.demo.entity.Booking;
import com.example.demo.entity.BookingGuest;

import javax.validation.constraints.NotNull;

public class GuestDto {
  private Booking booking;
  private BookingGuest guest;

  @NotNull
  public Booking getBooking() {
    return booking;
  }

  public void setBooking(Booking booking) {
    this.booking = booking;
  }

  @NotNull
  public BookingGuest getGuest() {
    return guest;
  }

  public void setGuest(BookingGuest guest) {
    this.guest = guest;
  }
}
