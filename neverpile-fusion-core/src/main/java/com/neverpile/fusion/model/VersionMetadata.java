package com.neverpile.fusion.model;

import java.time.Instant;

/**
 * Metadata for a version of a collection.
 */
public class VersionMetadata {
  /**
   * The time when the version of the collection was created. 
   */
  private Instant versionTimestamp;

  /**
   * The id of the {@link CollectionType} the version belongs to.
   */
  private String typeId;

  /**
   * The user who created the version of the collection.
   */
  private String createdBy;

  
  public VersionMetadata() {
  }
  
  public VersionMetadata(final Instant versionTimestamp, final String typeId, final String createdBy) {
    super();
    this.versionTimestamp = versionTimestamp;
    this.typeId = typeId;
    this.createdBy = createdBy;
  }

  public Instant getVersionTimestamp() {
    return versionTimestamp;
  }

  public void setVersionTimestamp(final Instant versionTimestamp) {
    this.versionTimestamp = versionTimestamp;
  }

  public String getTypeId() {
    return typeId;
  }

  public void setTypeId(final String typeId) {
    this.typeId = typeId;
  }

  public String getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(final String createdBy) {
    this.createdBy = createdBy;
  }
}
