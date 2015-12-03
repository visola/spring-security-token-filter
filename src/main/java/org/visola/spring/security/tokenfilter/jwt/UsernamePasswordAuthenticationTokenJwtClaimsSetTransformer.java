package org.visola.spring.security.tokenfilter.jwt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.nimbusds.jwt.JWTClaimsSet;

public class UsernamePasswordAuthenticationTokenJwtClaimsSetTransformer implements AuthenticationJwtClaimsSetTransformer {

  private static final String EMPTY_PASSWORD = "";

  private final Optional<String> rolePrefix;

  @Inject
  public UsernamePasswordAuthenticationTokenJwtClaimsSetTransformer(Optional<String> rolePrefix) {
    this.rolePrefix = rolePrefix;
  }

  @Override
  public JWTClaimsSet getClaimsSet(Authentication auth) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Authentication getAuthentication(JWTClaimsSet claimSet) {
    List<? extends GrantedAuthority> authorities = new ArrayList<>();
    String roles = (String) claimSet.getClaim("roles");
    if (roles != null) {
      authorities = Arrays.asList(roles.replaceAll("\\s*,\\s*", ",").split(",")).stream()
          .map(role -> new SimpleGrantedAuthority(rolePrefix.orElse("")+role.toUpperCase()))
          .collect(Collectors.toList());
    }
    return new UsernamePasswordAuthenticationToken(claimSet.getSubject(), EMPTY_PASSWORD, authorities);
  }

}
