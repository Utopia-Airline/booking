package com.example.demo.Exception;

import org.springframework.http.HttpStatus;

public class ErrorMessage {
  private int status;
  private String error;
  private String message;


  public ErrorMessage() {
  }

  public ErrorMessage(HttpStatus status, String message) {
    this.status = status.value();
    this.message = message;
    this.error = status.name();
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public String getError() {
    return error;
  }

  public void setError(String error) {
    this.error = error;
  }
}
