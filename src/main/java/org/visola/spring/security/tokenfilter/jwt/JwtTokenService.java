package org.visola.spring.security.tokenfilter.jwt;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.visola.spring.security.tokenfilter.TokenService;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

public class JwtTokenService implements TokenService {

  private static final String EMPTY_PASSWORD = "";

  private final String rolePrefix;
  private final JWSVerifier verifier;

  @Inject
  public JwtTokenService (String rolePrefix, @Value("${secret}") String secret) throws JOSEException {
    this.rolePrefix = rolePrefix;
    this.verifier = new MACVerifier(secret);
  }

  @Override
  public Optional<Authentication> verifyToken(Optional<String> token) {
    if (!token.isPresent()) {
      return Optional.empty();
    }

    SignedJWT signedJwt;
    JWTClaimsSet claimSet;
    try {
      signedJwt = SignedJWT.parse(token.get());
      if (!signedJwt.verify(verifier)) {
        throw new BadCredentialsException("Invalid token");
      }

      claimSet = signedJwt.getJWTClaimsSet();
    } catch (ParseException | JOSEException e) {
      throw new IllegalArgumentException("Error while parsing and verifying token.", e);
    }

    List<? extends GrantedAuthority> authorities = new ArrayList<>();
    String roles = (String) claimSet.getClaim("roles");
    if (roles != null) {
      authorities = Arrays.asList(roles.replaceAll("\\s*,\\s*", ",").split(",")).stream()
          .map(role -> new SimpleGrantedAuthority(rolePrefix+role.toUpperCase()))
          .collect(Collectors.toList());
    }

    return Optional.of(new UsernamePasswordAuthenticationToken(claimSet.getSubject(), EMPTY_PASSWORD, authorities));
  }

}