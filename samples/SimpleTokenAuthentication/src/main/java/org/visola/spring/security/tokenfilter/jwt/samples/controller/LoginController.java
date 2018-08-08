package org.visola.spring.security.tokenfilter.jwt.samples.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.visola.spring.security.tokenfilter.TokenService;
import org.visola.spring.security.tokenfilter.jwt.samples.model.LoginRequest;
import org.visola.spring.security.tokenfilter.jwt.samples.model.LoginResponse;

@RestController
public class LoginController {

  private final AuthenticationManager authenticationManager;
  private final TokenService tokenService;

  @Autowired
  public LoginController(AuthenticationManager authenticationManager, TokenService tokenService) {
    this.authenticationManager = authenticationManager;
    this.tokenService = tokenService;
  }

  @RequestMapping(value="/api/v1/login", method=RequestMethod.POST)
  @ResponseBody
  public LoginResponse login(@RequestBody LoginRequest loginRequest) {
    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
        loginRequest.getUsername(),
        loginRequest.getPassword()
    );

    Authentication authentication = authenticationManager.authenticate(authenticationToken);
    String token = tokenService.generateToken(authentication);
    return new LoginResponse(token);
  }

}
