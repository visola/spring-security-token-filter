package org.visola.spring.security.tokenfilter.jwt;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

@RunWith(MockitoJUnitRunner.class)
public class JwtTokenServiceTest {

  private static final String SECRET = "ONEVERYVERYVERYVERYVERYVERYVERYVERYVERYVERYVERYVERYLONGSECRET";
  private static final String USERNAME = "john";
  private static final User USER = new User(USERNAME,"", new ArrayList<>());
  private static final Date NOW = new Date();
  private static final Date EXPIRATION = new Date(Long.MAX_VALUE);
  private static final UsernamePasswordAuthenticationToken AUTHENTICATION = new UsernamePasswordAuthenticationToken(USER, "");

  @Mock private AuthenticationJwtClaimsSetTransformer mockTransformer;
  @Mock private JWSSigner mockSigner;
  @Mock private JWSVerifier mockVerifier;

  private AuthenticationJwtClaimsSetTransformer transformer;
  private JWSVerifier verifier;

  private JwtTokenService jwtTokenService;

  @Before
  public void setup() throws JOSEException {
    verifier = new MACVerifier(SECRET);

    transformer = new UsernamePasswordAuthenticationTokenJwtClaimsSetTransformer(Long.MAX_VALUE, Optional.empty());
    jwtTokenService = new JwtTokenService(transformer, SECRET);
  }

  @Test
  public void itShouldGenerateAValidToken() throws ParseException, JOSEException {
    JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
        .subject(USER.getUsername())
        .issueTime(NOW)
        .expirationTime(EXPIRATION)
        .build();

    // Method being tested
    String token = jwtTokenService.generateToken(AUTHENTICATION);

    SignedJWT signedJwt = SignedJWT.parse(token);
    JWTClaimsSet fetchedClaimsSet = signedJwt.getJWTClaimsSet();

    Assertions.assertThat(signedJwt.verify(verifier)).isTrue();
    System.out.println(signedJwt.serialize());
    Assertions.assertThat(fetchedClaimsSet.getSubject()).isEqualTo(USERNAME);
  }

  @Test(expected=RuntimeException.class)
  public void itShouldThrowExceptionIfSigningFails() throws Exception {
    Set<JWSAlgorithm> algorithms = new HashSet<>();
    algorithms.add(JWSAlgorithm.HS256);
    Mockito.when(mockSigner.supportedJWSAlgorithms()).thenReturn(algorithms);

    jwtTokenService = new JwtTokenService(mockTransformer, mockSigner, mockVerifier);

    JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
        .subject(USER.getUsername())
        .issueTime(NOW)
        .expirationTime(EXPIRATION)
        .build();

    Mockito.when(mockTransformer.getClaimsSet(AUTHENTICATION)).thenReturn(claimsSet);
    Mockito.when(mockSigner.sign(ArgumentMatchers.any(), ArgumentMatchers.any())).thenThrow(new JOSEException("Signing fail"));

    // Method being tested
    jwtTokenService.generateToken(AUTHENTICATION);
  }

  @Test
  public void itShouldVerifyAndTransformAValidToken() {
    String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqb2huIiwiZXhwIjo5MjIzMzcyMDM2ODU0Nzc1LCJpYXQiOjE0NTMxNTA5NDV9.cLgyGE9DoHqDhQTRzT6bm-mYOnTYCs80x70r3Tq7fOc";

    Optional<Authentication> maybeAuthentication = jwtTokenService.verifyToken(Optional.of(token));
    Assertions.assertThat(maybeAuthentication.isPresent()).isTrue();

    Authentication authentication = maybeAuthentication.get();
    Assertions.assertThat(authentication.getName()).isEqualTo(AUTHENTICATION.getName());
    Assertions.assertThat(authentication.getPrincipal()).isEqualTo(USERNAME);
  }

  @Test
  public void itShouldReturnEmptyAuthIfNotTokenProvided() {
    Optional<Authentication> maybeAuthentication = jwtTokenService.verifyToken(Optional.empty());
    Assertions.assertThat(maybeAuthentication.isPresent()).isFalse();
  }

  @Test(expected=BadCredentialsException.class)
  public void itShouldNotAceptExpiredToken() {
    String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqb2huIiwicm9sZXMiOltdLCJleHAiOi05MjIzMzcwNTgzNzAzMjIwLCJpYXQiOjE0NTMxNTE1NTV9.lmgcqbHZ46A55IQVF-vQTTXI8Y4R4FW_WdkZmTX7okA";
    jwtTokenService.verifyToken(Optional.of(token));
  }

  @Test(expected=BadCredentialsException.class)
  public void itShouldThrowBadCredentialsWithInvalidToken() {
    String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWV9.TJVA95OrM7E2cBab30RMHrHDcEfxjoYZgeFONFh7HgQ";
    jwtTokenService.verifyToken(Optional.of(token));
  }

  @Test(expected=IllegalArgumentException.class)
  public void itShouldThrowUnparseableToken() {
    String token = "INVALID_TOKEN";
    jwtTokenService.verifyToken(Optional.of(token));
  }

}
