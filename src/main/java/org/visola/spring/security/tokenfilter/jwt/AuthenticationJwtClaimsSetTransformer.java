package org.visola.spring.security.tokenfilter.jwt;

import org.springframework.security.core.Authentication;

import com.nimbusds.jwt.JWTClaimsSet;

public interface AuthenticationJwtClaimsSetTransformer {

  JWTClaimsSet getClaimsSet(Authentication auth);

  Authentication getAuthentication(JWTClaimsSet claimSet);

}
