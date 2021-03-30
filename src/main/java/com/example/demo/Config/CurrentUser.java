package com.example.demo.Config;

import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.entity.UserRole;
import org.springframework.stereotype.Component;

import javax.persistence.*;

@Component
public class CurrentUser {
  private Long id;
  private String username;
  private String givenName;
  private String familyName;
  private String email;
  private String phone;
  private Role role;

  public CurrentUser() {
  }

  public CurrentUser(Long id, String username, String givenName,
                     String familyName, String email, String phone, UserRole role) {
    this.id = id;
    this.username = username;
    this.givenName = givenName;
    this.familyName = familyName;
    this.email = email;
    this.phone = phone;
    this.role = Role.valueOf(role.getName());
  }

  public void setByUser(User user) {
    this.id = user.getId();
    this.username = user.getUsername();
    this.givenName = user.getGivenName();
    this.familyName = user.getFamilyName();
    this.email = user.getEmail();
    this.phone = user.getPhone();
    this.role = user.getRole();
  }

  public void clearUser() {
    this.id = null;
    this.username = null;
    this.givenName = null;
    this.familyName = null;
    this.email = null;
    this.phone = null;
    this.role = null;
  }

  public String getGivenName() {
    return givenName;
  }

  public void setGivenName(String givenName) {
    this.givenName = givenName;
  }

  public String getFamilyName() {
    return familyName;
  }

  public void setFamilyName(String familyName) {
    this.familyName = familyName;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public Role getRole() {
    return this.role;
  }

  public void setRole(UserRole role) {
    this.role = Role.valueOf(role.getName());
  }
}
