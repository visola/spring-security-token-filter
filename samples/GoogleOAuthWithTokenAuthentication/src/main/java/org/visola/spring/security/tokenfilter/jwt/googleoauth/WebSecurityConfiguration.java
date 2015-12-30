package org.visola.spring.security.tokenfilter.jwt.googleoauth;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;

@Configuration
@EnableWebMvcSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
    .authorizeRequests()

    // Authenticate endpoint can be access by anyone
    .antMatchers("/login.html").anonymous()

    // Secure the endpoints
    .antMatchers("/api/v1/*").hasAnyRole("USER");
  }

}
