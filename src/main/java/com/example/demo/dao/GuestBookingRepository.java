package com.example.demo.dao;

import com.example.demo.entity.GuestBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GuestBookingRepository extends JpaRepository<GuestBooking, Long> {
}
