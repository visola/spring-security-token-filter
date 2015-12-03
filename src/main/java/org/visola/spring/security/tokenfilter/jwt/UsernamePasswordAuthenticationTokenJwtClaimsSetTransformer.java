package org.visola.spring.security.tokenfilter.jwt;

import java.text.ParseException;
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

import net.minidev.json.JSONObject;

public class UsernamePasswordAuthenticationTokenJwtClaimsSetTransformer implements AuthenticationJwtClaimsSetTransformer {

  private static final String EMPTY_PASSWORD = "";

  private final Optional<String> rolePrefix;

  @Inject
  public UsernamePasswordAuthenticationTokenJwtClaimsSetTransformer(Optional<String> rolePrefix) {
    this.rolePrefix = rolePrefix;
  }

  @Override
  public JWTClaimsSet getClaimsSet(Authentication auth) {
    JSONObject object = new JSONObject();
    object.put("subject", auth.getPrincipal());

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

    object.put("roles", roles.toString());

    try {
      return JWTClaimsSet.parse(object);
    } catch (ParseException e) {
      throw new RuntimeException("Error while parsing JSON for claims set.", e);
    }
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
