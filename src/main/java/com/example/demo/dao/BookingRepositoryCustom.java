package com.example.demo.dao;

import com.example.demo.entity.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface BookingRepositoryCustom {
  Page<Booking> findAllByPassengers(Set<String> passengers, Pageable pageable);

  List<Booking> findAllByFilters(Map<String, String> filters, Pageable pageable);

  Page<Booking> findAllTypedQuery(String origin, String destination);

  public Page<Booking> findAllCriteriaQuery(Pageable pageable, Map<String, String> filters);
}
