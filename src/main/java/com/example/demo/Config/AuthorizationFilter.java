package com.example.demo.Config;

import com.example.demo.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

public class AuthorizationFilter extends OncePerRequestFilter {
  private static Logger LOGGER = LoggerFactory.getLogger(LoggingFilter.class);

  @Autowired
  private CurrentUser currentUser;

  @Autowired
  public AuthorizationFilter(CurrentUser currentUser) {
    this.currentUser = currentUser;
  }

  private String authUrl;


  @Override
  protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                  HttpServletResponse httpServletResponse,
                                  FilterChain filterChain) throws ServletException, IOException {
    HttpServletRequest request = (HttpServletRequest) httpServletRequest;
    HttpServletResponse response = (HttpServletResponse) httpServletResponse;
    LOGGER.info("AUTHORIZATION START");
    final Cookie[] cookies = httpServletRequest.getCookies();
    if (cookies != null && cookies.length >= 1) {
      final Optional<Cookie> sessionCookie = Arrays.stream(cookies)
        .filter(cookie -> cookie.getName().equals("session")).findFirst();
      if (sessionCookie.isPresent()) {
        // build HTTP headers, and body for sending to Auth microservice
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Cookie", sessionCookie.get().getName() + "=" + sessionCookie.get().getValue());
        HttpEntity authRequest = new HttpEntity(headers);
        try {
          ResponseEntity<User> responseEntity = restTemplate.exchange(authUrl, HttpMethod.GET, authRequest, User.class);
          if (responseEntity.getStatusCode().value() == 200 && responseEntity.getBody() != null) {
            LOGGER.info("User {} has logged in successfully", responseEntity.getBody().getGivenName());
            currentUser.setByUser(responseEntity.getBody());
          } else {
            LOGGER.error(responseEntity.getStatusCode().toString());
            currentUser.clearUser();
          }
        } catch (RestClientException e) {
          LOGGER.error("Exception while logging in " + e.getMessage());
          currentUser.clearUser();
        }
      } else {
        LOGGER.error("No Session cookie has found.");
        currentUser.clearUser();
      }
    } else {
      LOGGER.error("No Cookie Found.");
      currentUser.clearUser();
    }
    filterChain.doFilter(httpServletRequest, httpServletResponse);
    LOGGER.info("AUTHORIZATION DONE");
  }

  public void setAuthUrl(String authUrl) {
    this.authUrl = authUrl;
  }
//  @Override
//  protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
//    LOGGER.info("URL " + request.getRequestURI());
//    return request.getRequestURI().startsWith("/api/bookings/guests");
//  }
}
