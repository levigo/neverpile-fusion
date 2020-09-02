package com.neverpile.fusion.jpa.seen;

import java.time.Instant;
import java.util.Set;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Lob;
import javax.persistence.Table;

import com.neverpile.fusion.jpa.SemicolonDelimitedStringSetConverter;

/**
 * The entity definition representing a collection in the database.
 */
@Entity
@Table(name = "seen_unseen")
@IdClass(ContextKeyAndPrincipal.class)
public class SeenUnseenInfoEntity {
  @Id
  private String contextKey;

  @Id
  private String principalKey;

  /**
   * If set, marks all elements with a modification time stamp before or equal to the value as seen,
   * except for elements listed in {@link #unseenKeys}.
   */
  private Instant seenAllBefore;

  /**
   * Contains a list of element keys considered seen <em>in addition</em> to the ones covered by
   * {@link #seenAllBefore}.
   */
  @Convert(converter = SemicolonDelimitedStringSetConverter.class)
  @Lob
  private Set<String> seenKeys;

  /**
   * Contains a list of element keys considered unseen, even if their time stamp is older than
   * {@link #seenAllBefore}.
   * 
   * FIXME: maybe we don't actually need this
   */
  @Convert(converter = SemicolonDelimitedStringSetConverter.class)
  @Lob
  private Set<String> unseenKeys;

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

  public Instant getSeenAllBefore() {
    return seenAllBefore;
  }

  public void setSeenAllBefore(final Instant seenAllBefore) {
    this.seenAllBefore = seenAllBefore;
  }

  public Set<String> getSeenKeys() {
    return seenKeys;
  }

  public void setSeenKeys(final Set<String> seenKeys) {
    this.seenKeys = seenKeys;
  }

  public Set<String> getUnseenKeys() {
    return unseenKeys;
  }

  public void setUnseenKeys(final Set<String> unseenKeys) {
    this.unseenKeys = unseenKeys;
  }
}
