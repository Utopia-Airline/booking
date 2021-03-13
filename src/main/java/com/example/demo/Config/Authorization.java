package com.example.demo.Config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Authorization {
  @Bean
  public FilterRegistrationBean<LoggingFilter> loggingFilter() {
    FilterRegistrationBean<LoggingFilter> registrationBean
      = new FilterRegistrationBean<>();
    registrationBean.setFilter(new LoggingFilter());
    registrationBean.addUrlPatterns("/api/*");
    return registrationBean;
  }

}
