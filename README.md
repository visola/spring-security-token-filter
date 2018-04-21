**IMPORTANT!! This project is OBSELETE and DEPRECATED. Please use [Spring Security JOSE Framework](https://docs.spring.io/spring-security/site/docs/5.0.5.BUILD-SNAPSHOT/reference/htmlsingle/#jc-oauth2login).**

# spring-security-token-filter [![Build Status](https://travis-ci.org/visola/spring-security-token-filter.svg)](https://travis-ci.org/visola/spring-security-token-filter/builds)

Token authentication for Spring Security applications.

# Usage

Add the Spring Boot starter project to your classpath:

```groovy
repositories {
  mavenCentral()
}

dependencies {
  compile 'org.visola.spring.security:spring-security-token-filter-spring-boot-starter:1.1'
}
```

Add `TokenAuthenticationFilter` filter to your filter chain, like the following:

```java
// Imports omitted

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

  /**
    * The starter bundle will provide a TokenAuthenticationFilter for you.
    */
  @Autowired
  private TokenAuthenticationFilter tokenAuthenticationFilter;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    // This will make your app completely stateless
    http.csrf().disable()
      .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

    // Add the TokenAuthenticationFilter to your filter chain
    http.addFilterBefore(tokenAuthenticationFilter, BasicAuthenticationFilter.class);

    // More HttpSecurity configuration here
  }

}

```

## Not using Spring Boot?

Add the starter project as a dependency, then you just need to load the `JWTFilterConfiguration` configuration.

# JWT

*If you don't know what JWT is, you should read about it first at http://jwt.io/.*

If you're using Spring Boot and have the starter in your classpath, this will be taken care for you automatically.

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

# So what happens when a user logs in?

You need to create a token and give it back to the user somehow.


You can see examples in the sample apps, [here](samples/GoogleOAuthWithTokenAuthentication/src/main/java/org/visola/spring/security/tokenfilter/jwt/googleoauth/controller/GoogleOAuthController.java) and [here](samples/SimpleTokenAuthentication/src/main/java/org/visola/spring/security/tokenfilter/jwt/samples/controller/LoginController.java).
