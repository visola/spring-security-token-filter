package org.visola.spring.security.tokenfilter.jwt.googleoauth;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.IOException;

@Configuration
public class ApplicationConfiguration extends WebMvcConfigurerAdapter {

  @Bean
  public HttpClient httpClient() {
    return HttpClients.createDefault();
  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    // Static configuration to support Backbone's push state

    // All resources go to where they should go
    registry
        .addResourceHandler("/**/*.css", "/**/*.html", "/**/*.js", "/**/*.jsx", "/**/*.ttf", "/**/*.woff", "/**/*.woff2")
        .addResourceLocations("classpath:/static/");
  }

}
