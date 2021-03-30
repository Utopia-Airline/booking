package com.example.demo.Config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LoggingFilter implements Filter {
  private static Logger LOGGER = LoggerFactory.getLogger(LoggingFilter.class);

  @Override
  public void doFilter(ServletRequest servletRequest,
                       ServletResponse servletResponse,
                       FilterChain filterChain) throws IOException, ServletException {
    HttpServletRequest request = (HttpServletRequest) servletRequest;
    HttpServletResponse response = (HttpServletResponse) servletResponse;

//    LOGGER.info("Logging Request  {} : {}", request.getMethod(),
//      request.getRequestURI());
    filterChain.doFilter(servletRequest, servletResponse);
//    LOGGER.info("Filter done");
  }
}
