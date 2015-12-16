package org.visola.spring.security.tokenfilter.jwt.googleoauth.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.visola.spring.security.tokenfilter.jwt.googleoauth.model.Message;

@RestController
@RequestMapping("/api/v1/secure")
public class SecureController {

  @RequestMapping(method=RequestMethod.GET)
  public Message getMessage(@RequestParam(name="name", defaultValue="John") String name) {
    return new Message("Hello " + name + "!");
  }

}
