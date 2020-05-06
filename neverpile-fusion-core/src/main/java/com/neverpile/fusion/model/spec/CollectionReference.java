package com.neverpile.fusion.model.spec;

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
}
