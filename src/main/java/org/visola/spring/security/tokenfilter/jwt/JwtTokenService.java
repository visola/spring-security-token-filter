package org.visola.spring.security.tokenfilter.jwt;

import java.text.ParseException;
import java.util.Optional;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.visola.spring.security.tokenfilter.TokenService;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

public class JwtTokenService implements TokenService {

  private final AuthenticationJwtClaimsSetTransformer transformer;
  private final JWSVerifier verifier;

  @Inject
  public JwtTokenService (AuthenticationJwtClaimsSetTransformer transformer,
                          @Value("${secret}") String secret) throws JOSEException {
    this.verifier = new MACVerifier(secret);
    this.transformer = transformer;
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

    return Optional.of(transformer.getAuthentication(claimSet));
  }

}