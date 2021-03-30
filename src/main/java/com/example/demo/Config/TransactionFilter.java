package com.example.demo.Config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
@Order(1)
public class TransactionFilter implements Filter {
  private static Logger LOGGER = LoggerFactory.getLogger(LoggingFilter.class);

  @Override
  public void doFilter(
    ServletRequest request,
    ServletResponse response,
    FilterChain chain) throws IOException, ServletException {

    HttpServletRequest req = (HttpServletRequest) request;
//    LOGGER.info(
//      "Starting a transaction for req : {}",
//      req.getRequestURI());

    chain.doFilter(request, response);
//    LOGGER.info(
//      "Committing a transaction for req : {}",
//      req.getRequestURI());
  }
}