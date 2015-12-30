package org.visola.spring.security.tokenfilter.jwt.samples;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
      .authorizeRequests()

      // Authenticate endpoint can be access by anyone
      .antMatchers("/api/v1/login").anonymous()

      // All Others will be secure
      .antMatchers("/api/v1/**").hasAnyRole("USER");
  }

}
