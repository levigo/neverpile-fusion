package com.neverpile.fusion.rest;

import java.io.IOException;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.neverpile.common.openapi.OpenApiFragment;
import com.neverpile.common.openapi.ResourceOpenApiFragment;
import com.neverpile.common.openapi.ServersFragment;
import com.neverpile.fusion.api.SeenUnseenService;

/**
 * A configuration exposing the OpenAPI spec for the fusion REST API.
 */
@Configuration
public class OpenApiConfiguration {
  @Bean
  public OpenApiFragment coreOpenApiFragment() {
    return new ResourceOpenApiFragment("fusion", "core",
        new ClassPathResource("com/neverpile/fusion/fusion-core.yaml"));
  }

  @Bean
  public OpenApiFragment serversOpenApiFragment() throws IOException {
    return new ServersFragment("servers").withServer("/", "neverpile fusion");
  }
  
  @Configuration
  @ConditionalOnBean(SeenUnseenService.class)
  public static class SeenUnseenResourceConfiguration {
    @Bean
    public OpenApiFragment seenOpenApiFragment() {
      return new ResourceOpenApiFragment("fusion", "seen",
          new ClassPathResource("com/neverpile/fusion/seen-unseen.yaml"));
    }
  }
}
