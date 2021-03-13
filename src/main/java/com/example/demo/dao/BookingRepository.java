package com.example.demo.dao;

import com.example.demo.entity.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long>, BookingRepositoryCustom {

  Page<Booking> findByIsActive(@Param("isActive") Boolean isActive, Pageable pageable);

  Page<Booking> findAllByUserId(Long userId, Pageable pageable);

  Optional<Booking> findByConfirmationCode(String confirmationCode);

  Page<Booking> findAllByPassengersGivenNameContaining(@Param("fName") String fName, Pageable pageable);

  Page<Booking> findAllByFlightsRouteOriginCountryContainingAndFlightsRouteDestinationCountryContaining
    (@Param("origin") String origin,
     @Param("dest") String dest, Pageable pageable);

  @Query(value = "SELECT * FROM booking WHERE is_active=false", nativeQuery = true)
  Page<Booking> findAllWithFilterNative(@Param("origin") String origin,
                                        @Param("dest") String dest, Pageable pageable);

  @Query("SELECT b FROM Booking b JOIN b.flights f JOIN f.route.origin origin JOIN f.route.destination dest " +
    "WHERE (origin.country LIKE %:origin% OR origin.city LIKE %:origin% OR origin.name LIKE %:origin% " +
    "OR origin.iataId LIKE %:origin%) AND (dest.country LIKE %:dest% OR dest.city LIKE %:dest% OR " +
    "dest.name LIKE %:dest% OR dest.iataId LIKE %:dest%)")
  Page<Booking> findAllWithFilter(@Param("origin") String origin,
                                  @Param("dest") String dest, Pageable pageable);

}
