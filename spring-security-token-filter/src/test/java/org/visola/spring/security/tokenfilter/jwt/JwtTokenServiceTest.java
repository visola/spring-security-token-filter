package org.visola.spring.security.tokenfilter.jwt;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

@RunWith(MockitoJUnitRunner.class)
public class JwtTokenServiceTest {

  private static final String SECRET = "THISISASECRETTHATHASATLEAST256CHARS";

  private AuthenticationJwtClaimsSetTransformer transformer;
  private JWSVerifier verifier;
  private JwtTokenService jwtTokenService;

  @Before
  public void setup() throws JOSEException {
    verifier = new MACVerifier(SECRET);
    transformer = new UsernamePasswordAuthenticationTokenJwtClaimsSetTransformer(3600, Optional.empty());
    jwtTokenService = new JwtTokenService(transformer, SECRET);
  }

  @Test
  public void itShouldGenerateAValidToken() throws ParseException, JOSEException {
    String username = "john";
    User user = new User(username,"", new ArrayList<>());

    String token = jwtTokenService.generateToken(new UsernamePasswordAuthenticationToken(user, ""));

    SignedJWT signedJwt = SignedJWT.parse(token);
    JWTClaimsSet claimsSet = signedJwt.getJWTClaimsSet();

    Assertions.assertThat(signedJwt.verify(verifier)).isTrue();
    Assertions.assertThat(claimsSet.getSubject()).isEqualTo(username);
  }

}
