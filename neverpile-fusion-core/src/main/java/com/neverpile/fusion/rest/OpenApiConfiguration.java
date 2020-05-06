package com.neverpile.fusion.rest;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.neverpile.common.openapi.OpenApiFragment;
import com.neverpile.common.openapi.ResourceOpenApiFragment;
import com.neverpile.common.openapi.ServersFragment;

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
}
