package com.example.demo.service;

import com.example.demo.Config.CurrentUser;
import com.example.demo.dao.*;
import com.example.demo.dto.GuestDto;
import com.example.demo.entity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
public class BookingService {
  private final BookingRepository bookingRepository;
  private final GuestBookingRepository guestBookingRepository;
  private final UserBookingRepository userBookingRepository;
  private final BookingGuestRepository bookingGuestRepository;
  private final UserRepository userRepository;
  private final CurrentUser currentUser;
  private static final Logger LOGGER = LoggerFactory.getLogger(BookingService.class);

  @Autowired
  public BookingService(BookingRepository bookingRepository, GuestBookingRepository guestBookingRepository, UserBookingRepository userBookingRepository, BookingGuestRepository bookingGuestRepository, UserRepository userRepository, CurrentUser currentUser) {
    this.bookingRepository = bookingRepository;
    this.guestBookingRepository = guestBookingRepository;
    this.userBookingRepository = userBookingRepository;
    this.bookingGuestRepository = bookingGuestRepository;
    this.userRepository = userRepository;
    this.currentUser = currentUser;
  }

  @Transactional
  public Page<Booking> getAllPagedBookings(int page, int limit, Map<String, String> filters) {
    Pageable pageable = PageRequest.of(page, limit);
    return bookingRepository.findAllCriteriaQuery(pageable, filters);
  }

  @Transactional
  public Page<Booking> getAllBookingsForUser(Long userId, int page, int limit, Map<String, String> filters) {
    Pageable pageable = PageRequest.of(page, limit);
    return bookingRepository.findAllByUserId(userId, pageable);
  }

  @Transactional
  public Optional<Booking> getBookingById(Long id) {
    if (ifAdmin())
      return bookingRepository.findById(id);
    else {
      Optional<Booking> booking = bookingRepository.findById(id);
      if (booking.isPresent()) {
        Booking _booking = booking.get();
        // agent accessing her booking
        if (_booking.getAgent() != null && ifAgent(_booking.getAgent().getId()))
          return booking;
          // user accessing her booking
        else if (_booking.getUser() != null && ifOwner(_booking.getUser().getId()))
          return booking;
          // guest accessing her booking
        else if (_booking.getGuest() != null)
          return booking;
        else
          throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have enough privilege to access this resource");
      } else
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The booking was not found");
    }
  }

  @Transactional
  public void deleteBookingById(Long id) {
    Optional<Booking> booking = bookingRepository.findById(id);
    if (booking.isPresent()) {
      Booking b = booking.get();
      if (ifAdmin())
        bookingRepository.deleteById(id);
      else if (b.getUser() != null && ifOwner(b.getUser().getId()))
        bookingRepository.deleteById(id);
      else if (b.getAgent() != null && ifOwner(b.getAgent().getId()))
        bookingRepository.deleteById(id);
      else
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have enough privilege to access this resource");
    } else
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The booking was not found");
  }

  /**
   * we do not want to update a booking. only passengers can be modified
   * that should be taken care of at the passenger controller
   *
   * @param id
   * @param newBooking
   * @return
   */
  @Transactional
  public Booking updateBookingById(Long id, Booking newBooking) {
    Optional<Booking> booking = bookingRepository.findById(id);
    if (booking.isPresent()) {
      Booking b = booking.get();
//      b.updateBy(newBooking);
      if (ifAdmin())
        return bookingRepository.saveAndFlush(b);
      else if (b.getAgent() != null && ifAgent(b.getAgent().getId()))
        return bookingRepository.saveAndFlush(b);
      else if (b.getUser() != null && ifOwner(b.getUser().getId()))
        return bookingRepository.saveAndFlush(b);
      else
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have enough privilege to access this resource");
    } else
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The booking was not found");
  }

  /**
   * An agent can make a booking for a user
   * A user can make a booking for herself
   * user: required; agent: optional
   *
   * @param booking
   */
  @Transactional
  public void addBooking(Booking booking) {
    if (validateInsertion(booking)) {
      if (ifAdmin())
        bookingRepository.saveAndFlush(booking);
      else if (booking.getAgent() != null && ifAgent(booking.getAgent().getId()))
        bookingRepository.saveAndFlush(booking);
      else if (booking.getAgent() != null)
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have enough privilege to access this resource");
      else if (booking.getUser() != null && ifOwner(booking.getUser().getId()))
        bookingRepository.saveAndFlush(booking);
      else
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have enough privilege to access this resource");
    } else
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The body of the booking is not valid");
  }

  private boolean validateInsertion(Booking booking) {
    User user = booking.getUser();
    User agent = booking.getAgent();
    if (booking.getGuest() != null)
      return false;
    if (user != null && user.getId() != null) {
      Optional<User> _user = userRepository.findById(user.getId());
      if (_user.isEmpty() || _user.get().getRole() != Role.CUSTOMER)
        return false;
    } else
      return false;
    if (agent != null && agent.getId() != null) {
      Optional<User> _agent = userRepository.findById(agent.getId());
      if (_agent.isPresent() && _agent.get().getRole() == Role.AGENT)
        return true;
      else
        return false;
    } else
      return true;
  }

  ///TODO validate at least guest. agent optional on condition
  @Transactional
  public void addGuestBooking(GuestDto guestBooking) {
    Booking booking = guestBooking.getBooking();
    if (ifAdmin())
      createGuestBooking(guestBooking);
    else if (booking.getAgent() != null && ifAgent(booking.getAgent().getId()))
      createGuestBooking(guestBooking);
    else if (booking.getAgent() != null || booking.getUser() != null)
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have enough privilege to access this resource");
    else
      createGuestBooking(guestBooking);
  }

  private void createGuestBooking(GuestDto guestBooking) {
    Booking _booking = bookingRepository.saveAndFlush(guestBooking.getBooking());
    guestBooking.getGuest().setId(_booking.getId());
    bookingGuestRepository.saveAndFlush(guestBooking.getGuest());
  }

  public Optional<Booking> getBookingForGuest(String confirmationCode) {

    return bookingRepository.findByConfirmationCode(confirmationCode);
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

  @Transactional
  public Page<GuestBooking> getAllGuestBookingsLege(int page, int limit, Map<String, String> filters) {
    String _sort = filters.get("sort");
    Sort sort = (_sort != null) ? Sort.by(_sort).descending() : null;
    Pageable pageable = (sort != null) ? PageRequest.of(page, limit, sort) : PageRequest.of(page, limit);
    return guestBookingRepository.findAll(pageable);
  }
}

