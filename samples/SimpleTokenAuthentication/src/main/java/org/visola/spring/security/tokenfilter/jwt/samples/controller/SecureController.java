package org.visola.spring.security.tokenfilter.jwt.samples.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.visola.spring.security.tokenfilter.jwt.samples.model.Message;

@RequestMapping("/api/v1/secure")
@RestController
public class SecureController {

  @RequestMapping(method=RequestMethod.GET)
  public Message getMessage(@RequestParam(name="name", defaultValue="John") String name) {
    return new Message("Hello " + name + "!");
  }

}
