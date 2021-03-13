package com.example.demo.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthorizationConfiguration {
  private CurrentUser currentUser;

  @Autowired
  public AuthorizationConfiguration(CurrentUser currentUser) {
    this.currentUser = currentUser;
  }

  @Value("${auth.url}")
  private String authUrl;

  @Bean
  public FilterRegistrationBean<AuthorizationFilter> authorizationFilter() {
    FilterRegistrationBean<AuthorizationFilter> registrationBean
      = new FilterRegistrationBean<>();
    registrationBean.setFilter(new AuthorizationFilter(currentUser));
//    registrationBean.addUrlPatterns("/api/bookings/*");
    registrationBean.addUrlPatterns("/api/bookings/*");
    registrationBean.addUrlPatterns("/api/passengers/*");
    registrationBean.getFilter().setAuthUrl(authUrl);
    return registrationBean;
  }
}
