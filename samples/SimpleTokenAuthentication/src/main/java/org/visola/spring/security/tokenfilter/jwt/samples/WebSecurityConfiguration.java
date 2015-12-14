package org.visola.spring.security.tokenfilter.jwt.samples;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.visola.spring.security.tokenfilter.TokenService;
import org.visola.spring.security.tokenfilter.jwt.AuthenticationJwtClaimsSetTransformer;
import org.visola.spring.security.tokenfilter.TokenAuthenticationFilter;
import org.visola.spring.security.tokenfilter.jwt.JwtTokenService;
import org.visola.spring.security.tokenfilter.jwt.UsernamePasswordAuthenticationTokenJwtClaimsSetTransformer;

import com.nimbusds.jose.JOSEException;

@Configuration
@EnableWebMvcSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

  @Value("${secret}")
  String secret;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    // Make sure your endpoints are stateless, no session will be created
    http
    .csrf().disable()
    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

    http
    .authorizeRequests()

    // Authenticate endpoint can be access by anyone
    .antMatchers("/api/v1/login").anonymous()

    // All Others will be secure
    .antMatchers("/api/v1/**").hasAnyRole("USER");

    // Add the Toke Filter
    http.addFilterBefore(new TokenAuthenticationFilter(tokenService()), BasicAuthenticationFilter.class);
  }

  @Bean
  public TokenService tokenService() throws JOSEException {
    return new JwtTokenService(claimsSetTransformer(), secret);
  }

  @Bean
  public AuthenticationJwtClaimsSetTransformer claimsSetTransformer() {
    return new UsernamePasswordAuthenticationTokenJwtClaimsSetTransformer(TimeUnit.HOURS.toMillis(8), Optional.of("ROLE_"));
  }

}
