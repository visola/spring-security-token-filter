# spring-security-token-filter [![Build Status](https://travis-ci.org/visola/spring-security-token-filter.svg)](https://travis-ci.org/visola/spring-security-token-filter/builds)

Token authentication for Spring Security applications.

# Usage

Add the Spring Boot starter project to your classpath and you're done:

```
repositories {
  mavenCentral()
}

dependencies {
  compile 'org.visola.spring.security:spring-security-token-filter-boot-starter:1.0'
}
``` 

## Not using Spring Boot?

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
