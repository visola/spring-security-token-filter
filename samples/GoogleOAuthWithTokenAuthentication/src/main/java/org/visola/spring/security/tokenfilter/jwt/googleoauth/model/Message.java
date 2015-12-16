package org.visola.spring.security.tokenfilter.jwt.googleoauth.model;

public class Message {

  private String message;

  public Message(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

}
