package org.visola.spring.security.tokenfilter.jwt;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.visola.spring.security.tokenfilter.TokenAuthenticationFilter;
import org.visola.spring.security.tokenfilter.TokenService;

import com.nimbusds.jose.JOSEException;

@AutoConfigureBefore(WebSecurityConfigurerAdapter.class)
@Configuration
@ConditionalOnWebApplication
public class JWTFilterConfiguration {

  @Value("${security.token.filter.secret}")
  String secret;

  @Value("${security.token.filter.role-prefix:ROLE_}")
  String rolePrefix;

  @Value("${security.token.filter.token-duration-in-minutes:0}")
  int tokenDurationInMinutes;

  @Value("${security.token.filter.token-duration-in-hours:8}")
  int tokenDurationInHours;

  @Bean
  @ConditionalOnMissingBean(TokenAuthenticationFilter.class)
  public TokenAuthenticationFilter tokenAuthenticationFilter(TokenService tokenService) {
    return new TokenAuthenticationFilter(tokenService);
  }

  @Bean
  @ConditionalOnMissingBean(TokenService.class)
  public TokenService tokenService() throws JOSEException {
    return new JwtTokenService(claimsSetTransformer(), secret);
  }

  @Bean
  @ConditionalOnMissingBean(AuthenticationJwtClaimsSetTransformer.class)
  public AuthenticationJwtClaimsSetTransformer claimsSetTransformer() {
    Optional<String> prefix = Optional.empty();

    if (!"".equals(rolePrefix)) {
      prefix = Optional.of(rolePrefix);
    }

    long tokenDuration = 0;

    if (tokenDurationInHours != 0) {
      tokenDuration = TimeUnit.HOURS.toMillis(tokenDurationInHours);
    }

    if (tokenDurationInMinutes != 0) {
      tokenDuration = TimeUnit.MINUTES.toMillis(tokenDurationInMinutes);
    }

    return new UsernamePasswordAuthenticationTokenJwtClaimsSetTransformer(tokenDuration, prefix);
  }

}