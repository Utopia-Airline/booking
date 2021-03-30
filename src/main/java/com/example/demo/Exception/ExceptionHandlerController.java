package com.example.demo.Exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ExceptionHandlerController {
  private static Logger LOGGER = LoggerFactory.getLogger(ExceptionHandlerController.class);

  @Bean
  public ErrorAttributes errorAttributes() {
    // Hide exceptions field in the return object
    return new DefaultErrorAttributes() {
      @Override
      public Map<String, Object> getErrorAttributes(
        WebRequest webRequest,
        ErrorAttributeOptions options) {
        Map<String, Object> errorAttributes = new HashMap<String, Object>();
        Object errorStatus = webRequest.getAttribute(RequestDispatcher.ERROR_STATUS_CODE, RequestAttributes.SCOPE_REQUEST);
        Object errorMessage = webRequest.getAttribute(RequestDispatcher.ERROR_MESSAGE, RequestAttributes.SCOPE_REQUEST);
        if (errorMessage != null) {
          errorAttributes.put("status", errorStatus);
          errorAttributes.put("message", errorMessage);
        }
        return errorAttributes;
      }
    };
  }

  @ExceptionHandler(HttpServerErrorException.class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public ErrorMessage handleHttpServerErrorException(HttpServerErrorException ex,
                                                     HttpServletResponse res) throws IOException {
    LOGGER.error(ex.getStatusCode().value() + " " + ex.getStatusText());
    return new ErrorMessage(ex.getStatusCode(), ex.getStatusText());
  }

  @ExceptionHandler(NoHandlerFoundException.class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public ErrorMessage handleNoHandlerFoundException(HttpServerErrorException ex,
                                                    HttpServletResponse res) throws IOException {
    LOGGER.error(ex.getStatusCode().value() + " " + ex.getStatusText());
    return new ErrorMessage(ex.getStatusCode(), ex.getStatusText());
  }
}
