package com.neverpile.fusion.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neverpile.fusion.api.CollectionTypeService;
import com.neverpile.fusion.api.exception.NeverpileException;
import com.neverpile.fusion.model.CollectionType;

/**
 * An implementation of {@link CollectionTypeService} which pulls collection definitions from
 * <code>.json</code> or <code>.yaml</code> files residing in some spring {@link Resource} location.
 * The files must be named after the collection type id with an added suffix of <code>.json</code>
 * for JSON-encoded definitions or <code>.yaml</code> or <code>.yml</code> for YAML encoded ones.
 * <p>
 * For Spring-DI configured components, the resource path is injected from the property
 * <code>neverpile-fusion.resource-path-collection-type-service.base-path</code> and the component
 * must be enabled by setting the property
 * <code>neverpile-fusion.resource-path-collection-type-service.enabled=true</code>. The constructor
 * injection expects to beans of typ {@link ObjectMapper}, the default one for JSON, and one, with
 * the qualifier <code>yaml</code>, configured for YAML.
 */
@Component
@ConditionalOnProperty(name = "neverpile-fusion.resource-path-collection-type-service.enabled", matchIfMissing = false, havingValue = "true")
public class ResourcePathCollectionTypeService implements CollectionTypeService {
  private static final Logger LOGGER = LoggerFactory.getLogger(ResourcePathCollectionTypeService.class);

  private final Resource basePath;
  private final ObjectMapper jsonMapper;
  private final ObjectMapper yamlMapper;

  @Autowired
  public ResourcePathCollectionTypeService(
      @Value("${neverpile-fusion.resource-path-collection-type-service.base-path}") final Resource basePath,
      final ObjectMapper jsonMapper, @Qualifier("yaml") final ObjectMapper yamlMapper) {
    this.basePath = basePath;
    this.jsonMapper = jsonMapper;
    this.yamlMapper = yamlMapper;
  }

  @Override
  public Optional<CollectionType> get(final String id) {
    try {
      Resource resource = basePath.createRelative(id + ".yaml");
      if (resource.isReadable())
        return unmarshal(resource, yamlMapper);

      resource = basePath.createRelative(id + ".yml");
      if (resource.isReadable())
        return unmarshal(resource, yamlMapper);

      resource = basePath.createRelative(id + ".json");
      if (resource.isReadable())
        return unmarshal(resource, jsonMapper);

      return Optional.empty();
    } catch (IOException e) {
      throw new NeverpileException("Failed to retrieve collection type", e);
    }
  }

  private Optional<CollectionType> unmarshal(final Resource resource, final ObjectMapper mapper) throws IOException {
    return Optional.of(mapper.readValue(resource.getInputStream(), CollectionType.class));
  }

  @Override
  public List<CollectionType> getAllTypes() {
    try {
      ArrayList<CollectionType> types = new ArrayList<>();
      for (File f : basePath.getFile().listFiles(f -> f.isFile() && f.getName().matches(".*\\.(ya?ml|json)$"))) {
        try {
          unmarshal(basePath.createRelative(f.getName()),
              f.getName().endsWith("json") ? jsonMapper : yamlMapper).ifPresent(types::add);
        } catch (IOException e) {
          LOGGER.info("Can't unmarshal Collection type {} - skipping it", f.getName(), e);
        }
      }
      return types;
    } catch (IOException e) {
      throw new NeverpileException("Can't list collection types", e);
    }
  }

  @Override
  public void save(final CollectionType type) {
    throw new UnsupportedOperationException("Collection types are supposed to be edited locally");
  }

  @Override
  public boolean delete(final String id) {
    throw new UnsupportedOperationException("Collection types are supposed to be edited locally");
  }

}
