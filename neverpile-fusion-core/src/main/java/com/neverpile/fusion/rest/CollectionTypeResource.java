package com.neverpile.fusion.rest;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.neverpile.fusion.api.CollectionTypeService;
import com.neverpile.fusion.model.CollectionType;
import com.neverpile.fusion.rest.exception.NotFoundException;

import io.micrometer.core.annotation.Timed;

/**
 * A REST resource providing access to the system's {@link CollectionType}s. It is backed by a
 * {@link CollectionTypeService} implementation. For details see the OpenAPI specification in
 * /neverpile-fusion-core/src/main/resources/com/neverpile/fusion/fusion-core.yaml
 */
@RestController
@RequestMapping(path = "/api/v1/collection-types", produces = {
    MediaType.APPLICATION_JSON_VALUE
})
public class CollectionTypeResource {

  @Autowired
  private CollectionTypeService collectionTypeService;

  @GetMapping(value = "{id}")
  @Timed(description = "get collection type by id", extraTags = {
      "operation", "retrieve", "target", "collection-type"
  }, value = "fusion.collection-type.get")
  public CollectionType get(@PathVariable("id") final String id) {
    return collectionTypeService.get(id).orElseThrow(() -> new NotFoundException("Collection type not found"));
  }

  @GetMapping()
  @Timed(description = "get type list", extraTags = {
      "operation", "retrieve", "target", "collection-type"
  }, value = "fusion.collection-type.all")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  public List<CollectionTypeListEntry> getAllTypes() {
    return collectionTypeService.getAllTypes().stream().map(
        t -> new CollectionTypeListEntry(t.getId(), t.getName(), t.getDescription())).collect(Collectors.toList());
  }

  public static class CollectionTypeListEntry {
    private String id;
    private String description;
    private final String name;

    public CollectionTypeListEntry(final String id, final String name, final String description) {
      this.id = id;
      this.name = name;
      this.description = description;
    }

    public String getId() {
      return id;
    }

    public void setId(final String id) {
      this.id = id;
    }

    public String getDescription() {
      return description;
    }

    public void setDescription(final String description) {
      this.description = description;
    }

    public String getName() {
      return name;
    }
  }

  @PostMapping()
  @Timed(description = "save collection type", extraTags = {
      "operation", "store", "target", "collection-type"
  }, value = "fusion.collection-type.save")
  public CollectionType save(@RequestBody final CollectionType type) {
    if (type.getId() == null)
      type.setId(UUID.randomUUID().toString());

    collectionTypeService.save(type);

    return type;
  }
}
