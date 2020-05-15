package com.neverpile.fusion.configuration;

import java.time.Clock;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Provide a {@link Clock}-Bean if none is provided. Clocks are injected in order to improve testability.
 */
@Configuration
public class ClockConfiguration {
  @ConditionalOnMissingBean(Clock.class)
  @Bean
  public Clock systemClock() {
    return Clock.systemDefaultZone();
  }
}
