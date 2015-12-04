package org.visola.spring.security.tokenfilter.jwt;

import java.text.ParseException;
import java.util.Optional;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.visola.spring.security.tokenfilter.TokenService;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

public class JwtTokenService implements TokenService {

  private final AuthenticationJwtClaimsSetTransformer transformer;
  private final JWSSigner signer;
  private final JWSVerifier verifier;

  @Inject
  public JwtTokenService (AuthenticationJwtClaimsSetTransformer transformer,
                          @Value("${secret}") String secret) throws JOSEException {
    this.signer = new MACSigner(secret);
    this.verifier = new MACVerifier(secret);
    this.transformer = transformer;
  }

  @Override
  public String generateToken(Authentication authentication) {
    JWTClaimsSet claimsSet = transformer.getClaimsSet(authentication);
    SignedJWT signedJwt = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
    try {
      signedJwt.sign(signer);
    } catch (JOSEException e) {
      throw new RuntimeException("Error while signing token.", e);
    }
    return signedJwt.serialize();
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
      claimSet = signedJwt.getJWTClaimsSet();

      if (!signedJwt.verify(verifier)) {
        throw new BadCredentialsException("Invalid token");
      }
    } catch (ParseException | JOSEException e) {
      throw new IllegalArgumentException("Error while parsing and verifying token.", e);
    }


    if (claimSet.getExpirationTime().getTime() < System.currentTimeMillis()) {
      throw new BadCredentialsException("Token is expired");
    }

    return Optional.of(transformer.getAuthentication(claimSet));
  }

}