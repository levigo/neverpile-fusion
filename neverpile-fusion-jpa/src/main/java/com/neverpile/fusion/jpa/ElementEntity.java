package com.neverpile.fusion.jpa;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Convert;
import javax.persistence.Embeddable;
import javax.persistence.Lob;
import javax.persistence.Table;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * The entity definition representing a collection element in the database.
 */
@Embeddable
@Table(name = "collection_elements")
public class ElementEntity {
  private String id;

  private Instant dateCreated;

  private Instant dateModified;

  @Convert(converter = SemicolonDelimitedStringListConverter.class)
  private List<String> tags = new ArrayList<>();

  @Convert(converter = JsonNodeConverter.class)
  @Lob
  private ObjectNode metadata;

  @Convert(converter = JsonNodeConverter.class)
  @Lob
  private JsonNode specification;

  public String getId() {
    return id;
  }

  public void setId(final String id) {
    this.id = id;
  }

  public ObjectNode getMetadata() {
    return metadata;
  }

  public void setMetadata(final ObjectNode metadata) {
    this.metadata = metadata;
  }

  public Instant getDateCreated() {
    return dateCreated;
  }

  public void setDateCreated(final Instant creationDate) {
    this.dateCreated = creationDate;
  }

  public Instant getDateModified() {
    return dateModified;
  }

  public void setDateModified(final Instant modificationDate) {
    this.dateModified = modificationDate;
  }

  public JsonNode getSpecification() {
    return specification;
  }

  public void setSpecification(final JsonNode specification) {
    this.specification = specification;
  }

  public List<String> getTags() {
    return tags;
  }

  public void setTags(final List<String> tags) {
    this.tags = tags;
  }
}
