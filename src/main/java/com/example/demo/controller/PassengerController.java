package com.example.demo.controller;

import com.example.demo.entity.Passenger;
import com.example.demo.service.PassengerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/passengers")
public class PassengerController {
  private final PassengerService passengerService;

  @Autowired
  public PassengerController(PassengerService passengerService) {
    this.passengerService = passengerService;
  }

  @PutMapping("/{id}")
  public Passenger updatePassengerById(@PathVariable("id") Long id, @RequestBody Passenger passenger) {
    return passengerService.updatePassengerById(id, passenger);
  }
}
