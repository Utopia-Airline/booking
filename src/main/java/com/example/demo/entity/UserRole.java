package com.example.demo.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "user_role")
public class UserRole {
  private static final long serialVersionUID = 1L;
  @Id
  @Column(name = "id")
  Long id;
  @Column(name = "name")
  String name;

  public UserRole(Long id, String name) {
    this.id = id;
    this.name = name;
  }

  public UserRole(String name) {
    String role = name.toUpperCase();
    switch (role) {
      case "ADMIN":
        this.id = 1L;
        break;
      case "Customer":
        this.id = 2L;
        break;
      case "AGENT":
        this.id = 3L;
        break;
      default:
        this.id = null;
    }
    ;
    this.name = name;
  }

  protected UserRole() {

  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof UserRole)) return false;
    UserRole userRole = (UserRole) o;
    return getId().equals(userRole.getId()) && getName().equals(userRole.getName());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getName());
  }
}