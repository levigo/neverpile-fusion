package com.neverpile.fusion.jpa;

import java.time.temporal.ChronoUnit;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("neverpile-fusion.jpa")
public class JPAConfiguration {
  private ChronoUnit timestampResolution = ChronoUnit.MILLIS;

  public ChronoUnit getTimestampResolution() {
    return timestampResolution;
  }

  public void setTimestampResolution(final ChronoUnit timestampResolution) {
    this.timestampResolution = timestampResolution;
  }
}
