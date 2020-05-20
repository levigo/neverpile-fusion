package com.neverpile.fusion.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

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
  
  private ResourcePathCollectionTypeServiceConfiguration resourcePathCollectionTypeService;

  public ResourcePathCollectionTypeServiceConfiguration getResourcePathCollectionTypeService() {
    return resourcePathCollectionTypeService;
  }

  public void setResourcePathCollectionTypeService(final ResourcePathCollectionTypeServiceConfiguration resourcePathCollectionTypeService) {
    this.resourcePathCollectionTypeService = resourcePathCollectionTypeService;
  }
}
