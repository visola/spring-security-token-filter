package org.visola.spring.security.tokenfilter.jwt.googleoauth;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GoogleOAuthAuthenticationApplication {

  public static void main (String [] args) {
    SpringApplication.run(GoogleOAuthAuthenticationApplication.class, args);
  }

  @Bean
  public HttpClient httpClient() {
    return HttpClients.createDefault();
  }

}
