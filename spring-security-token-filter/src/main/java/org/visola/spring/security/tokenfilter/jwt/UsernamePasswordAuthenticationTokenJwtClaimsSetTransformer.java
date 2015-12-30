package org.visola.spring.security.tokenfilter.jwt;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.nimbusds.jwt.JWTClaimsSet;

public class UsernamePasswordAuthenticationTokenJwtClaimsSetTransformer implements AuthenticationJwtClaimsSetTransformer {

  private static final String EMPTY_PASSWORD = "";
  private static final String ROLES_FIELD = "roles";

  private final long tokenDuration;
  private final Optional<String> rolePrefix;

  @Inject
  public UsernamePasswordAuthenticationTokenJwtClaimsSetTransformer(long tokenDuration, Optional<String> rolePrefix) {
    this.rolePrefix = rolePrefix;
    this.tokenDuration = tokenDuration;
  }

  @Override
  public JWTClaimsSet getClaimsSet(Authentication auth) {
    UserDetails user = (UserDetails) auth.getPrincipal();
    long now = System.currentTimeMillis();

    List<String> roles = user.getAuthorities().stream()
      .map(a -> {
        String role = a.getAuthority();
        if (rolePrefix.isPresent()) {
          role = role.substring(rolePrefix.get().length(), role.length());
        }
        return role;
      })
      .collect(Collectors.toList());

    return new JWTClaimsSet.Builder()
        .subject(user.getUsername())
        .issueTime(new Date(now))
        .expirationTime(new Date(now + tokenDuration))
        .claim(ROLES_FIELD, roles)
        .build();
  }

  @Override
  public Authentication getAuthentication(JWTClaimsSet claimsSet) {
    List<? extends GrantedAuthority> authorities = getAuthorities(claimsSet);
    return new UsernamePasswordAuthenticationToken(claimsSet.getSubject(), EMPTY_PASSWORD, authorities);
  }

  @SuppressWarnings("unchecked")
  private List<? extends GrantedAuthority> getAuthorities(JWTClaimsSet claimsSet) {
    List<String> roles = (List<String>) claimsSet.getClaim(ROLES_FIELD);
    if (roles != null) {
      return roles.stream()
          .map(role -> new SimpleGrantedAuthority(rolePrefix.orElse("")+role.toUpperCase()))
          .collect(Collectors.toList());
    }
    return new ArrayList<>();
  }

}
