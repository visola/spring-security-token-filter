package org.visola.spring.security.tokenfilter;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

public class TokenAuthenticationFilter extends GenericFilterBean {

  private final TokenService tokenService;

  public TokenAuthenticationFilter(TokenService tokenService) {
    this.tokenService = tokenService;
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    HttpServletRequest httpRequest = (HttpServletRequest) request;

    Optional<Authentication> authentication = tokenService.validateToken(Optional.ofNullable(httpRequest.getHeader("X-Auth-Token")));
    if (authentication.isPresent()) {
      SecurityContextHolder.getContext().setAuthentication(authentication.get());
    }

    chain.doFilter(httpRequest, response);
  }

}
