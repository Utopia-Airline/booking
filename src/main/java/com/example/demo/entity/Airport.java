package com.example.demo.entity;

import javax.persistence.*;

@Entity
@Table(name = "airport")
public class Airport {
  @Id
  @Column(name = "iata_id")
  private String iataId;
  @Column(name = "name", nullable = false)
  String name;
  @Column(name = "city", nullable = false)
  String city;
  @Column(name = "country", nullable = false)
  String country;
  @Column(name = "longitude", nullable = false)
  Double longitude;
  @Column(name = "latitude", nullable = false)
  Double latitude;
  @Column(name = "altitude", nullable = false)
  Double altitude;
  @Column(name = "timezone", nullable = false)
  Integer timezone;

  public Airport() {
  }

  public String getIataId() {
    return iataId;
  }

  public void setIataId(String iataId) {
    this.iataId = iataId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public Double getLongitude() {
    return longitude;
  }

  public void setLongitude(Double longitude) {
    this.longitude = longitude;
  }

  public Double getLatitude() {
    return latitude;
  }

  public void setLatitude(Double latitude) {
    this.latitude = latitude;
  }

  public Double getAltitude() {
    return altitude;
  }

  public void setAltitude(Double altitude) {
    this.altitude = altitude;
  }

  public Integer getTimezone() {
    return timezone;
  }

  public void setTimezone(Integer timezone) {
    this.timezone = timezone;
  }
}
