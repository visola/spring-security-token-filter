# spring-security-token-filter [![Build Status](https://travis-ci.org/visola/spring-security-token-filter.svg)](https://travis-ci.org/visola/spring-security-token-filter/builds)

Token authentication for Spring Security applications.

# Usage

To use the token filter you just need to add it to your Spring Security filter chain like the following:

```java
@Configuration
@EnableWebMvcSecurity
public class MyWebSecurityConfiguration extends WebSecurityConfigurerAdapter {

  //...

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    // Setup Spring Security authorization here

    // Add the Token Filter
    http.addFilterBefore(new TokenAuthenticationFilter(tokenService()), BasicAuthenticationFilter.class);
  }

  //...

}
```

You only need to provide a `TokenService` implementation, or use the JWT one, as shown below.

## JWT

*If you don't know what JWT is, you should read about it first at http://jwt.io/.*

To make your life easier, this library has a `TokenService` implementation that works out of the box with the JWT specification using the [Nimbus JOSE + JWT](http://connect2id.com/products/nimbus-jose-jwt) implementation. To use it you just need to register the `JwtTokenService` which uses an interface (`AuthenticationJwtClaimsSetTransformer`) to map between JWT claims set to Spring Security Authentication. The following sample code is using the default (out-of-the-box) implementation:

```java
@Bean
public TokenService tokenService() throws JOSEException {
  return new JwtTokenService(claimsSetTransformer(), secret);
}

@Bean
public AuthenticationJwtClaimsSetTransformer claimsSetTransformer() {
  // How long will your token last and the prefix for roles
  return new UsernamePasswordAuthenticationTokenJwtClaimsSetTransformer(TimeUnit.HOURS.toMillis(8), Optional.of("ROLE_"));
}
```
