package com.neverpile.fusion.jpa;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Convert;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Lob;
import javax.persistence.Table;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * The entity definition representing a collection in the database.
 */
@Entity
@Table(name = "collection_versions")
@IdClass(IdAndVersionTimestamp.class)
public class CollectionEntity {
  @Id
  private String id;

  @Id
  private Instant versionTimestamp;

  private String type;

  @Convert(converter = JsonNodeConverter.class)
  @Lob
  private ObjectNode metadata = null;

  private Instant dateCreated;

  private Instant dateModified;
  
  private String createdBy;

  @ElementCollection(fetch = FetchType.EAGER)
  private List<ElementEntity> elements = new ArrayList<>();

  public String getId() {
    return id;
  }

  public void setId(final String id) {
    this.id = id;
  }

  public Instant getVersionTimestamp() {
    return versionTimestamp;
  }

  public void setVersionTimestamp(final Instant versionTimestamp) {
    this.versionTimestamp = versionTimestamp;
  }

  public String getType() {
    return type;
  }

  public void setType(final String type) {
    this.type = type;
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

  public void setDateCreated(final Instant dateCreated) {
    this.dateCreated = dateCreated;
  }

  public Instant getDateModified() {
    return dateModified;
  }

  public void setDateModified(final Instant dateModified) {
    this.dateModified = dateModified;
  }

  public List<ElementEntity> getElements() {
    return elements;
  }

  public void setElements(final List<ElementEntity> elements) {
    this.elements = elements;
  }

  public String getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(final String createdBy) {
    this.createdBy = createdBy;
  }
}
