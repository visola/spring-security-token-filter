package org.visola.spring.security.tokenfilter.jwt;

import com.nimbusds.jose.JOSEException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.visola.spring.security.tokenfilter.TokenAuthenticationFilter;
import org.visola.spring.security.tokenfilter.TokenService;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@AutoConfigureBefore(WebSecurityConfigurerAdapter.class)
@Configuration
@ConditionalOnWebApplication
@Order(37)
public class JWTFilterConfiguration extends WebSecurityConfigurerAdapter {

  @Value("${security.token.filter.secret}")
  String secret;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.csrf().disable()
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

    http.addFilterBefore(new TokenAuthenticationFilter(tokenService()), BasicAuthenticationFilter.class);
  }

  @Bean
  @ConditionalOnMissingBean(TokenService.class)
  public TokenService tokenService() throws JOSEException {
    return new JwtTokenService(claimsSetTransformer(), secret);
  }

  @Bean
  @ConditionalOnMissingBean(AuthenticationJwtClaimsSetTransformer.class)
  public AuthenticationJwtClaimsSetTransformer claimsSetTransformer() {
    return new UsernamePasswordAuthenticationTokenJwtClaimsSetTransformer(TimeUnit.HOURS.toMillis(8), Optional.of("ROLE_"));
  }

}