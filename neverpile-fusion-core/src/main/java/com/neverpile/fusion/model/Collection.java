package com.neverpile.fusion.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * A collection represents a set of elements which are relevant in a certain business or domain
 * context. There are several possible types of elements:
 * <ul>
 * <li>general artifacts like a document, an image or a media stream,
 * <li>reference to another collection within the same system,
 * <li>paged media (e.g. PDFs, scanned images, etc.) which can be arbitrarily composed from any
 * number of sources to form a virtual document.
 * </ul>
 * Collections are generally versioned, so that updates do not replace the old state but simply add
 * a new version with old versions still being accessible for reference purposes.
 */
@JsonPropertyOrder({
    "id", "versionTimestamp", "type", "state", "dateCreated", "dateModified", "createdBy", "metadata", "elements"
})
public class Collection {
  /**
   * The collection's unique id. In general, an id is just a string. Implementations may however,
   * subject ids to a certain format like UUIDs, numeric values etc.
   */
  private String id;

  /**
   * The time when this version of the collection was created. The version timestamp must be unique
   * among all versions of a collection with a certain id.
   */
  private Instant versionTimestamp;

  /**
   * The id of the {@link CollectionType} this collection belongs to.
   */
  private String typeId;

  /**
   * The elements of the collection
   */
  private List<Element> elements = new ArrayList<>();

  /**
   * The metadata of the collection. Metadata can consist of arbitrary JSON, the shcema of which is
   * type or domain specific.
   */
  private ObjectNode metadata;

  /**
   * The date when the collection was created.
   */
  private Instant dateCreated;

  /**
   * The time when the collection was last modified. In contrast to {@link #versionTimestamp} this
   * property holds the business-relevant time which may be different from the technical version
   * timestamp.
   */
  private Instant dateModified;

  /**
   * The user who created this particular version of the collection.
   */
  private String createdBy;

  public enum State {
    /**
     * The collection is currently in active use. New versions can be create at any time.
     */
    Active,
    /**
     * The collection is closed. It can be viewed, but creating new versions and thus changing its
     * state is restricted to users holding special permissions.
     */
    Closed,
    /**
     * The collection is marked for deletion. Viewing the collection is restricted to users holding
     * special permissions.
     */
    MarkedForDeletion
  }

  /**
   * The state of the collection (with the last/current version of the collection representing the
   * current state).
   */
  private State state = State.Active;

  public String getId() {
    return id;
  }

  public void setId(final String id) {
    this.id = id;
  }

  public Instant getVersionTimestamp() {
    return versionTimestamp;
  }

  public void setVersionTimestamp(final Instant modificationDate) {
    this.versionTimestamp = modificationDate;
  }

  public String getTypeId() {
    return typeId;
  }

  public void setTypeId(final String type) {
    this.typeId = type;
  }

  public State getState() {
    return state;
  }

  public void setState(final State state) {
    this.state = state;
  }

  public List<Element> getElements() {
    return elements;
  }

  public void setElements(final List<Element> elements) {
    this.elements = elements;
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

  public void setDateModified(final Instant dateModified) {
    this.dateModified = dateModified;
  }

  public String getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(final String createdBy) {
    this.createdBy = createdBy;
  }
}
