package com.neverpile.fusion.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.neverpile.fusion.model.spec.Specification;

/**
 * Elements hold or refer to the actual content of a collection.
 */
@JsonPropertyOrder({
    "id", "type", "dateCreated", "dateModified", "tags", "metadata", "specification"
})
public class Element {
  /**
   * The element's unique id. In general, an id is just a string. Implementations may however,
   * subject ids to a certain format like UUIDs, numeric values etc. The id must be unique among all
   * elements of the collection.
   */
  private String id;

  /**
   * The metadata of the element. Metadata can consist of arbitrary JSON, the shcema of which is
   * type or domain specific.
   */
  private ObjectNode metadata;

  /**
   * The date when the element was created.
   */
  private Instant dateCreated;

  /**
   * The time when the element was last modified.
   */
  private Instant dateModified;

  /**
   * The element's tags. Tags describe an element and are usually used to build a view layout for a
   * collection.
   */
  private List<String> tags = new ArrayList<>();

  /**
   * The specification of the type and source of the element content. 
   */
  private Specification specification;

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

  public List<String> getTags() {
    return tags;
  }

  public void setTags(final List<String> tags) {
    this.tags = tags;
  }

  public Specification getSpecification() {
    return specification;
  }

  public void setSpecification(final Specification specification) {
    this.specification = specification;
  }
}
