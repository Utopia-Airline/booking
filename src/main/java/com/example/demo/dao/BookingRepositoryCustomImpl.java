package com.example.demo.dao;

import com.example.demo.entity.Booking;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import javax.persistence.criteria.*;
import javax.transaction.Transactional;
import java.util.*;

@Repository
public class BookingRepositoryCustomImpl implements BookingRepositoryCustom {

  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public Page<Booking> findAllByPassengers(Set<String> passengers, Pageable pageable) {
    return null;
  }

  @Transactional
  @Override
  public List<Booking> findAllByFilters(Map<String, String> filters, Pageable pageable) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Booking> query = cb.createQuery(Booking.class);
    Root<Booking> bookingRoot = query.from(Booking.class);
    Path<String> originPath = bookingRoot.get("origin");
    List<Predicate> predicates = new ArrayList<>();
    for (String key : filters.keySet()) {
      predicates.add(cb.like(originPath, key));
    }
    query.select(bookingRoot).where(cb.or(predicates.toArray(new Predicate[predicates.size()])));
    return entityManager.createQuery(query).getResultList();
  }

  @Transactional
  @Override
  public Page<Booking> findAllCriteriaQuery(Pageable pageable, Map<String, String> filters) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Booking> cq = cb.createQuery(Booking.class);
    Root<Booking> bookingRoot = cq.from(Booking.class);
    // build associations
    Join<Object, Object> flights = bookingRoot.join("flights");
    Path<Object> origin = flights.join("route").get("origin");
    Path<Object> dest = flights.join("route").get("destination");
    if (filters != null) {
      if (filters.containsKey("type") && Objects.equals(filters.get("type").toLowerCase(), "user")) {
        Path<Object> guest = bookingRoot.join("user", JoinType.INNER);
      } else if (filters.containsKey("type") && Objects.equals(filters.get("type").toLowerCase(), "guest")) {
        Path<Object> guest = bookingRoot.join("guest", JoinType.INNER);
      }
    }
    // build predicates
    List<Predicate> predicates = new ArrayList<>();
    if (filters != null) {
      if (filters.containsKey("origin")) {
        String val = "%" + filters.get("origin") + "%";
        Predicate originPredicate = cb.or(cb.like(
          origin.get("country"), val), cb.like(origin.get("city"), val),
          cb.like(origin.get("iataId"), val), cb.like(origin.get("name"), val));
        predicates.add(originPredicate);
      }
      if (filters.containsKey("destination")) {
        String val = "%" + filters.get("destination") + "%";
        Predicate originPredicate = cb.or(cb.like(
          dest.get("country"), val), cb.like(dest.get("city"), val),
          cb.like(dest.get("iataId"), val), cb.like(dest.get("name"), val));
        predicates.add(originPredicate);
      }
      if (filters.containsKey("isActive")) {
        boolean val = Boolean.valueOf(filters.get("isActive"));
        Predicate isActivePredicate = cb.equal(bookingRoot.get("isActive"), val);
        predicates.add(isActivePredicate);
      }
    }


    cq.where(predicates.stream().toArray(Predicate[]::new));
    // build sort
    if (filters.containsKey("sort") && Objects.equals(filters.get("sort").toLowerCase(), "departuretime")) {
      if (filters.containsKey("order") && Objects.equals(filters.get("sort").toLowerCase(), "asc"))
        cq.orderBy(cb.asc(flights.get("departureTime")));
      else
        cq.orderBy(cb.desc(flights.get("departureTime")));
    }
    TypedQuery<Booking> tq = entityManager.createQuery(cq).setFirstResult((int) pageable.getOffset())
      .setMaxResults(pageable.getPageSize());

    // get total count for pagination
    Long total = findCount(filters);

    // build booking
    Page<Booking> bookings = null;
    try {
      bookings = new PageImpl<>(tq.getResultList(), pageable, total);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return bookings;
  }

  private Long findCount(Map<String, String> filters) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
    Root<Booking> bookingRoot = countQuery.from(Booking.class);
    Join<Object, Object> flights = bookingRoot.join("flights");
    Path<Object> origin = flights.join("route").get("origin");
    Path<Object> dest = flights.join("route").get("destination");
    if (filters != null) {
      if (filters.containsKey("type") && Objects.equals(filters.get("type").toLowerCase(), "user")) {
        Path<Object> guest = bookingRoot.join("user", JoinType.INNER);
      } else if (filters.containsKey("type") && Objects.equals(filters.get("type").toLowerCase(), "guest")) {
        Path<Object> guest = bookingRoot.join("guest", JoinType.INNER);
      }
    }
    // build predicates
    List<Predicate> predicates = new ArrayList<>();
    if (filters != null) {
      if (filters.containsKey("origin")) {
        String val = "%" + filters.get("origin") + "%";
        Predicate originPredicate = cb.or(cb.like(
          origin.get("country"), val), cb.like(origin.get("city"), val),
          cb.like(origin.get("iataId"), val), cb.like(origin.get("name"), val));
        predicates.add(originPredicate);
      }
      if (filters.containsKey("destination")) {
        String val = "%" + filters.get("destination") + "%";
        Predicate originPredicate = cb.or(cb.like(
          dest.get("country"), val), cb.like(dest.get("city"), val),
          cb.like(dest.get("iataId"), val), cb.like(dest.get("name"), val));
        predicates.add(originPredicate);
      }
      if (filters.containsKey("isActive")) {
        boolean val = Boolean.valueOf(filters.get("isActive"));
        Predicate isActivePredicate = cb.equal(bookingRoot.get("isActive"), val);
        predicates.add(isActivePredicate);
      }
    }
    countQuery.select(cb.count(bookingRoot)).where(predicates.stream().toArray(Predicate[]::new));
    Long total = entityManager.createQuery(countQuery).getSingleResult();
    return total;
  }


  @Transactional
  @Override
  public Page<Booking> findAllTypedQuery(String origin, String destination) {
    Page<Booking> bookings = null;
    String queryCount = "SELECT COUNT(*) as total\n" +
      "FROM booking\n" +
      "JOIN flight_bookings fb on booking.id = fb.booking_id\n" +
      "JOIN flight flights on fb.flight_id = flights.id\n" +
      "JOIN route r on flights.route_id = r.id\n" +
      "JOIN airport origin on r.origin_id = origin.iata_id\n" +
      "JOIN airport dest on r.destination_id = dest.iata_id\n" +
      "LEFT JOIN booking_user bu on booking.id = bu.booking_id\n" +
      "LEFT JOIN user u on bu.user_id = u.id\n" +
      "LEFT JOIN booking_agent ba on booking.id = ba.booking_id\n" +
      "LEFT JOIN user agent on ba.agent_id = agent.id\n" +
      "LEFT JOIN booking_guest bg on booking.id = bg.booking_id " +
      "WHERE (origin.country LIKE :origin OR origin.city LIKE :origin " +
      "OR origin.iata_id LIKE :origin OR origin.name LIKE :origin)" +
      "AND (dest.country LIKE :destination OR dest.city LIKE :destination " +
      "OR dest.iata_id LIKE :destination OR dest.name LIKE :destination)";
    String query = "SELECT *\n" +
      "FROM booking\n" +
      "JOIN flight_bookings fb on booking.id = fb.booking_id\n" +
      "JOIN flight flights on fb.flight_id = flights.id\n" +
      "JOIN route r on flights.route_id = r.id\n" +
      "JOIN airport origin on r.origin_id = origin.iata_id\n" +
      "JOIN airport dest on r.destination_id = dest.iata_id\n" +
      "LEFT JOIN booking_user bu on booking.id = bu.booking_id\n" +
      "LEFT JOIN user u on bu.user_id = u.id\n" +
      "LEFT JOIN booking_agent ba on booking.id = ba.booking_id\n" +
      "LEFT JOIN user agent on ba.agent_id = agent.id\n" +
      "LEFT JOIN booking_guest bg on booking.id = bg.booking_id " +
      "WHERE (origin.country LIKE :origin OR origin.city LIKE :origin " +
      "OR origin.iata_id LIKE :origin OR origin.name LIKE :origin)" +
      "AND (dest.country LIKE :destination OR dest.city LIKE :destination " +
      "OR dest.iata_id LIKE :destination OR dest.name LIKE :destination)";
    EntityTransaction et = entityManager.getTransaction();
    try {
      et.begin();
      Query tq = entityManager.createNativeQuery(queryCount);
      tq.setParameter("origin", "%" + origin + "%");
      tq.setParameter("destination", "%" + destination + "%");
      Integer total = Integer.parseInt(tq.getSingleResult().toString());
      tq = entityManager.createNativeQuery(query, Booking.class);
      tq.setParameter("origin", "%" + origin + "%");
      tq.setParameter("destination", "%" + destination + "%");
      tq.setFirstResult(0);
      tq.setMaxResults(20);
      Pageable pageable = PageRequest.of(0, 30);
      bookings = new PageImpl<>(tq.getResultList(), pageable, total);
      et.commit();
    } catch (Exception e) {
      et.rollback();
    }
    return bookings;
  }
}
