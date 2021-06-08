package com.neverpile.fusion.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.neverpile.common.locking.LockService;

@Component
@ConfigurationProperties("neverpile-fusion")
public class ApplicationConfiguration {
  public static class ResourcePathCollectionTypeServiceConfiguration {
    private boolean enabled;

    private Resource basePath;

    public boolean isEnabled() {
      return enabled;
    }

    public void setEnabled(final boolean enabled) {
      this.enabled = enabled;
    }

    public Resource getBasePath() {
      return basePath;
    }

    public void setBasePath(final Resource basePath) {
      this.basePath = basePath;
    }
  }

  public static class Locking {
    public enum Mode {
      /**
       * Resources do not actively participate in locking. Clients may perform cooperative locking
       * using a {@link LockService} or other external means, but resources only preven lost updates
       * using optimistic locking/versioning.
       */
      OPTIMISTIC,
      /**
       * Mutating calls to resources require a lock token via the
       * <code>X-Neverpile-Lock-Token</code>-header, otherwise they are rejected.
       */
      EXPLICIT,
      /**
       * Mutating calls to resources without a lock token will cause resources to actively acquire a
       * lock token on the client's behalf.
       */
      IMPLICIT
    }

    /**
     * The locking mode to use. Default: {@link Mode#OPTIMISTIC}
     */
    private Mode mode = Mode.OPTIMISTIC;

    public Mode getMode() {
      return mode;
    }

    public void setMode(Mode mode) {
      this.mode = mode;
    }
  }

  private ResourcePathCollectionTypeServiceConfiguration resourcePathCollectionTypeService;

  private Locking locking = new Locking();

  public ResourcePathCollectionTypeServiceConfiguration getResourcePathCollectionTypeService() {
    return resourcePathCollectionTypeService;
  }

  public void setResourcePathCollectionTypeService(
      final ResourcePathCollectionTypeServiceConfiguration resourcePathCollectionTypeService) {
    this.resourcePathCollectionTypeService = resourcePathCollectionTypeService;
  }

  public Locking getLocking() {
    return locking;
  }

  public void setLocking(Locking locking) {
    this.locking = locking;
  }
}
