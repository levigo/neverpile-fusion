package com.neverpile.fusion.jpa.collection;

import java.io.Serializable;
import java.time.Instant;

/**
 * A key class for the versioned collection entities.
 */
public class IdAndVersionTimestamp implements Serializable {
  private static final long serialVersionUID = 1L;

  private String id;

  private Instant versionTimestamp;
  
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
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((versionTimestamp == null) ? 0 : versionTimestamp.hashCode());
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    IdAndVersionTimestamp other = (IdAndVersionTimestamp) obj;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    if (versionTimestamp == null) {
      if (other.versionTimestamp != null)
        return false;
    } else if (!versionTimestamp.equals(other.versionTimestamp))
      return false;
    return true;
  }
}
