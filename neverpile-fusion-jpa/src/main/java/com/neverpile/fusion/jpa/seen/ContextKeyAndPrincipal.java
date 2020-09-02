package com.neverpile.fusion.jpa.seen;

import java.io.Serializable;

/**
 * A key class for the seen/unseen info entities.
 */
public class ContextKeyAndPrincipal implements Serializable {
  private static final long serialVersionUID = 1L;

  private String contextKey;

  private String principalKey;

  public ContextKeyAndPrincipal() {
  }
  
  public ContextKeyAndPrincipal(final String contextKey, final String principalKey) {
    super();
    this.contextKey = contextKey;
    this.principalKey = principalKey;
  }

  public String getContextKey() {
    return contextKey;
  }

  public void setContextKey(final String contextKey) {
    this.contextKey = contextKey;
  }

  public String getPrincipalKey() {
    return principalKey;
  }

  public void setPrincipalKey(final String principalKey) {
    this.principalKey = principalKey;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((contextKey == null) ? 0 : contextKey.hashCode());
    result = prime * result + ((principalKey == null) ? 0 : principalKey.hashCode());
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
    ContextKeyAndPrincipal other = (ContextKeyAndPrincipal) obj;
    if (contextKey == null) {
      if (other.contextKey != null)
        return false;
    } else if (!contextKey.equals(other.contextKey))
      return false;
    if (principalKey == null) {
      if (other.principalKey != null)
        return false;
    } else if (!principalKey.equals(other.principalKey))
      return false;
    return true;
  }
}
