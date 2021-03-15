package com.example.demo.controller;

import com.example.demo.Config.CurrentUser;
import com.example.demo.dto.GuestDto;
import com.example.demo.entity.Booking;
import com.example.demo.service.BookingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {
  private static Logger LOGGER = LoggerFactory.getLogger(BookingController.class);
  private final BookingService bookingService;
  private final CurrentUser currentUser;

  @Autowired
  public BookingController(BookingService bookingService, CurrentUser currentUser) {
    this.bookingService = bookingService;
    this.currentUser = currentUser;
  }

  // ====================================================== bookings =================================================
  @GetMapping
  public Page<Booking> getAllBookings(@RequestParam(defaultValue = "0", required = false) int page,
                                      @RequestParam(defaultValue = "20", required = false) int limit,
                                      @RequestParam(required = false) Map<String, String> filters) {
    LOGGER.info("{} invokes getALlBookings", currentUser.getRole());
    if (bookingService.ifAdmin())
      return bookingService.getAllPagedBookings(page, limit, filters);
    else
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have enough privilege to access this resource");
  }

  @GetMapping("/{id}")
  public Booking getBookingById(@PathVariable("id") Long id) {
    return bookingService.getBookingById(id).orElseThrow(() ->
      new ResponseStatusException(HttpStatus.NOT_FOUND, "The booking was not found"));
  }

  ///TODO user add; guest add; agent add
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public void postBooking(@RequestBody @Valid Booking booking) {
    bookingService.addBooking(booking);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteBookingById(@PathVariable("id") Long id) {
    bookingService.deleteBookingById(id);
  }

  //TODO user update her own booking
  @PutMapping("/{id}")
  public Booking updateBookingById(@PathVariable("id") Long id, @RequestBody Booking booking) {
    return bookingService.updateBookingById(id, booking);
  }

  // ====================================================== bookings =================================================
  // ====================================================== guests =================================================

  ///TODO unify with one post; agent books for guest
  @PostMapping("/guest")
  @ResponseStatus(HttpStatus.CREATED)
  public void postGuestBooking(@RequestBody @Valid GuestDto guestBooking) {
    bookingService.addGuestBooking(guestBooking);
  }

  @GetMapping("/guest")
  public Booking getBookingForGuest(@RequestParam String confirmationCode) {
    return bookingService.getBookingForGuest(confirmationCode).orElseThrow(() ->
      new ResponseStatusException(HttpStatus.NOT_FOUND, "The booking was not found"));
  }

  // ====================================================== guests =================================================
  // ====================================================== users =================================================

  @GetMapping("/user")
  public Page<Booking> getAllBookingsForUser(@RequestParam Long userId,
                                             @RequestParam(defaultValue = "0", required = false) int page,
                                             @RequestParam(defaultValue = "20", required = false) int limit,
                                             @RequestParam(required = false) Map<String, String> filters) {
    if (bookingService.ifOwnerOrAdmin(userId))
      return bookingService.getAllBookingsForUser(userId, page, limit, filters);
    else
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have enough privilege to access this resource");
  }
  @GetMapping("/agent")
  public Page<Booking> getAllBookingsForAgent(@RequestParam Long agentId,
                                             @RequestParam(defaultValue = "0", required = false) int page,
                                             @RequestParam(defaultValue = "20", required = false) int limit,
                                             @RequestParam(required = false) Map<String, String> filters) {
    if (bookingService.ifOwnerOrAdmin(agentId))
      return bookingService.getAllBookingsForAgent(agentId, page, limit, filters);
    else
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have enough privilege to access this resource");
  }
  // ====================================================== users =================================================
}
