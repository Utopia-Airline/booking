package com.example.demo.service;

import com.example.demo.Config.CurrentUser;
import com.example.demo.dao.BookingRepository;
import com.example.demo.dao.PassengerRepository;
import com.example.demo.entity.Booking;
import com.example.demo.entity.Passenger;
import com.example.demo.entity.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.Objects;
import java.util.Optional;

@Service
public class PassengerService {
  private final PassengerRepository passengerRepository;
  private final BookingRepository bookingRepository;
  private final CurrentUser currentUser;
  private static final Logger LOGGER = LoggerFactory.getLogger(PassengerService.class);

  @Autowired
  public PassengerService(PassengerRepository passengerRepository, BookingRepository bookingRepository, CurrentUser currentUser) {
    this.passengerRepository = passengerRepository;
    this.bookingRepository = bookingRepository;
    this.currentUser = currentUser;
  }

  @Transactional
  public Page<Passenger> getAllPassengers(int page, int limit) {
    Pageable pageable = PageRequest.of(page, limit);
    return passengerRepository.findAll(pageable);
  }

  @Transactional
  public Optional<Passenger> getPassengerById(Long id) {
    return passengerRepository.findById(id);
  }

  @Transactional
  public Passenger updatePassengerById(Long id, Passenger newPassenger) {
    Optional<Passenger> passengerOp = passengerRepository.findById(id);
    if (passengerOp.isPresent()) {
      Passenger passenger = passengerOp.get();
      Booking booking = passenger.getBooking();
      passenger.updateBy(newPassenger);
      if (ifAdmin())
        return passengerRepository.saveAndFlush(passenger);
      else if (booking.getAgent() != null && ifAgent(booking.getAgent().getId()))
        return passengerRepository.saveAndFlush(passenger);
      else if (booking.getUser() != null && ifOwner(booking.getUser().getId()))
        return passengerRepository.saveAndFlush(passenger);
      else
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have enough privilege to access this resource");
    } else throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The booking was not found");
  }

  public boolean ifOwnerOrAdmin(Long userId) {
    return (currentUser != null && (currentUser.getRole() == Role.ADMIN || Objects.equals(currentUser.getId(), userId)));
  }

  public boolean ifAdmin() {
    return (currentUser != null && currentUser.getRole() == Role.ADMIN);
  }

  public boolean ifOwner(Long userId) {
    return (currentUser != null && Objects.equals(currentUser.getId(), userId));
  }

  public boolean ifAgent(Long agentId) {
    return (currentUser != null && currentUser.getRole() == Role.AGENT
      && Objects.equals(currentUser.getId(), agentId));
  }
}
