package com.neverpile.fusion.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.JsonNode;
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
   * The metadata of the element. Metadata can consist of arbitrary JSON, the schema of which is
   * type or domain specific.
   */
  private JsonNode metadata;

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

  public JsonNode getMetadata() {
    return metadata;
  }

  public void setMetadata(final JsonNode metadata) {
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

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((dateCreated == null) ? 0 : dateCreated.hashCode());
    result = prime * result + ((metadata == null) ? 0 : metadata.hashCode());
    result = prime * result + ((specification == null) ? 0 : specification.hashCode());
    result = prime * result + ((tags == null) ? 0 : tags.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Element other = (Element) obj;
    return Objects.equals(id, other.id) //
        && Objects.equals(dateCreated, other.dateCreated) // we ignore date modified!
        && Objects.equals(metadata, other.metadata) //
        && Objects.equals(specification, other.specification) //
        && Objects.equals(tags, other.tags);
  }
}
