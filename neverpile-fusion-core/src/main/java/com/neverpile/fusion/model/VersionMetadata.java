package com.neverpile.fusion.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Metadata for a version or several versions of a collection.
 */
public class VersionMetadata {
  /**
   * The time when the version(s) of the collection were created. 
   */
  private List<Instant> versionTimestamps;

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
    this.versionTimestamps = new ArrayList<Instant>(4);
    this.versionTimestamps.add(versionTimestamp);
    this.typeId = typeId;
    this.createdBy = createdBy;
  }
  
  public VersionMetadata(final List<Instant> versionTimestamps, final String typeId, final String createdBy) {
    super();
    this.versionTimestamps = versionTimestamps;
    this.typeId = typeId;
    this.createdBy = createdBy;
  }

  public List<Instant> getVersionTimestamps() {
    return versionTimestamps;
  }

  public void setVersionTimestamps(final List<Instant> versionTimestamps) {
    this.versionTimestamps = versionTimestamps;
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
