package com.neverpile.fusion.model.spec;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * A reference to another collection within the same system.
 */
@JsonTypeName("artifact")
public class CollectionReference extends Specification {
  /**
   * The id of the referenced collection.
   */
  private String collectionId;

  public String getCollectionId() {
    return collectionId;
  }

  public void setCollectionId(final String dossierId) {
    this.collectionId = dossierId;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((collectionId == null) ? 0 : collectionId.hashCode());
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
    CollectionReference other = (CollectionReference) obj;
    return Objects.equals(collectionId, other.collectionId);
  }
}
