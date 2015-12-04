package org.visola.spring.security.tokenfilter.jwt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import com.nimbusds.jwt.JWTClaimsSet;

public class UsernamePasswordAuthenticationTokenJwtClaimsSetTransformer implements AuthenticationJwtClaimsSetTransformer {

  private static final String EMPTY_PASSWORD = "";

  private final long tokenDuration;
  private final Optional<String> rolePrefix;

  @Inject
  public UsernamePasswordAuthenticationTokenJwtClaimsSetTransformer(long tokenDuration, Optional<String> rolePrefix) {
    this.rolePrefix = rolePrefix;
    this.tokenDuration = tokenDuration;
  }

  @Override
  public JWTClaimsSet getClaimsSet(Authentication auth) {
    User user = (User) auth.getPrincipal();
    long now = System.currentTimeMillis();

    StringBuilder roles = new StringBuilder();
    for (GrantedAuthority authority : auth.getAuthorities()) {
      String role = authority.getAuthority();
      if (rolePrefix.isPresent()) {
        role = role.substring(rolePrefix.get().length(), role.length());
      }
      roles.append(role.toLowerCase());
      roles.append(",");
    }
    roles.setLength(roles.length() - 1);

    return new JWTClaimsSet.Builder()
        .subject(user.getUsername())
        .issueTime(new Date(now))
        .expirationTime(new Date(now + tokenDuration))
        .claim("roles", roles.toString())
        .build();
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
