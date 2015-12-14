package org.visola.spring.security.tokenfilter.jwt.googleoauth.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.visola.spring.security.tokenfilter.TokenService;
import org.visola.spring.security.tokenfilter.jwt.googleoauth.model.LoginResponse;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Controller
public class GoogleOAuthController {

  private static final String UTF8 = StandardCharsets.UTF_8.name();
  private static final String CSRF_TOKEN_COOKIE_NAME = "CSRFTOKEN";
  private static final String GOOGLE_OAUTH_ENDPOINT = "https://accounts.google.com/o/oauth2/auth";
  private static final String GOOGLE_TOKEN_ENDPOINT = "https://www.googleapis.com/oauth2/v3/token";
  private static final String GOOGLE_EMAIL_ENDPOINT = "https://www.googleapis.com/plus/v1/people/me";
  private static final String SCOPES = "email";

  private final String clientId;
  private final String clientSecret;
  private final String redirectUri;
  private final HttpClient httpClient;
  private final ObjectMapper objectMapper;
  private final TokenService tokenService;
  private final Set<String> emails = new HashSet<>();

  @Autowired
  public GoogleOAuthController(HttpClient httpClient,
                               ObjectMapper objectMapper,
                               TokenService tokenService,
                               @Value("${oauth.google.clientId}") String clientId,
                               @Value("${oauth.google.clientSecret}") String clientSecret,
                               @Value("${oauth.google.redirectUri}") String redirectUri) {
    this.clientId = clientId;
    this.clientSecret = clientSecret;
    this.redirectUri = redirectUri;
    this.httpClient = httpClient;
    this.objectMapper = objectMapper;
    this.tokenService = tokenService;
  }

  @RequestMapping(method= RequestMethod.GET, value="/authenticate/google")
  public String redirectToGoogle(HttpServletResponse response) throws UnsupportedEncodingException {
    // Set CSRF token
    String csrfToken = UUID.randomUUID().toString();
    response.addCookie(createCsrfTokenCookie(csrfToken, (int) TimeUnit.MINUTES.toMillis(1)));

    StringBuffer uri = new StringBuffer("redirect:");
    uri.append(GOOGLE_OAUTH_ENDPOINT);
    uri.append("?response_type=code&scope=");
    uri.append(URLEncoder.encode(SCOPES, UTF8));
    uri.append("&client_id=");
    uri.append(URLEncoder.encode(clientId, UTF8));
    uri.append("&redirect_uri=");
    uri.append(URLEncoder.encode(redirectUri, UTF8));

    // In the state we send the CSRF token
    uri.append("&state=");
    uri.append(URLEncoder.encode(String.format("%s", csrfToken), UTF8));

    return uri.toString();
  }

  @RequestMapping(method=RequestMethod.GET, value="/authenticate/oauth2callback")
  public ModelAndView receiveRedirect(String code,
                                      String state,
                                      HttpServletResponse response,
                                      @CookieValue(CSRF_TOKEN_COOKIE_NAME) String csrfToken) throws Exception {

    // Remove CSRF token
    response.addCookie(createCsrfTokenCookie(null, 0));

    // State stores CSRF token
    if (!csrfToken.equals(state)) {
      throw new AccessDeniedException("Invalid CSRF token.");
    }

    ModelAndView mv = new ModelAndView("oauth2callback");

    String email = getUserEmail(getToken(code));

    if (!emails.contains(email)) {
      emails.add(email);
    }

    LoginResponse authResponse = new LoginResponse(tokenService.generateToken(new UsernamePasswordAuthenticationToken(new User(email, "", new ArrayList<>()), "")));

    mv.addObject("email", email);
    mv.addObject("token", authResponse.getToken());
    return mv;
  }

  private String getUserEmail(String token) throws Exception {
    HttpGet get = new HttpGet(GOOGLE_EMAIL_ENDPOINT);
    get.addHeader("Authorization", String.format("Bearer %s", token));

    HttpResponse response = httpClient.execute(get);
    JsonNode node = objectMapper.readTree(response.getEntity().getContent());
    for (JsonNode email : node.get("emails")) {
      if (email.get("type").asText().equals("account")) {
        return email.get("value").asText();
      }
    }

    throw new RuntimeException("Can't find user email.");
  }

  private String getToken(String code) throws Exception {
    List<NameValuePair> formParams = new ArrayList<>();
    formParams.add(new BasicNameValuePair("code", code));
    formParams.add(new BasicNameValuePair("client_id", clientId));
    formParams.add(new BasicNameValuePair("client_secret", clientSecret));
    formParams.add(new BasicNameValuePair("redirect_uri", redirectUri));
    formParams.add(new BasicNameValuePair("grant_type", "authorization_code"));

    HttpPost post = new HttpPost(GOOGLE_TOKEN_ENDPOINT);
    post.setEntity(new UrlEncodedFormEntity(formParams, Consts.UTF_8));
    HttpResponse response = httpClient.execute(post);

    if (response.getStatusLine().getStatusCode() >= 200 && response.getStatusLine().getStatusCode() < 300) {
      JsonNode node = objectMapper.readTree(response.getEntity().getContent());
      return node.get("access_token").asText();
    } else {
      throw new RuntimeException(String.format("Error while fetching token from Google. Status: %d, Response: %s", response.getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase()));
    }
  }

  private Cookie createCsrfTokenCookie(String csrfToken, int age) {
    Cookie cookie = new Cookie(CSRF_TOKEN_COOKIE_NAME, csrfToken);
    cookie.setMaxAge(age);
    return cookie;
  }

}
