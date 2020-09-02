package com.neverpile.fusion.model.seen;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * A SeenUnseenInfo contains generic information about seen or unseen (read or unread) elements
 * within a certain context. The management of seen/unseen elements is based on the following
 * assumptions:
 * <ul>
 * <li>All elements are uniquely identifiable by some key which is just a string.
 * <li>Elements have a modification timestamp which optionally allows all elements before some
 * timestamp to be efficiently labeled as seen.
 * </ul>
 */
public class SeenUnseenInfo {
  /**
   * If set, marks all elements with a modification time stamp before or equal to the value as seen,
   * except for elements listed in {@link #unseenKeys}.
   */
  private Instant seenAllBefore;

  /**
   * Contains a list of element keys considered seen <em>in addition</em> to the ones covered by
   * {@link #seenAllBefore}.
   */
  private Set<String> seenKeys = new HashSet<>();

  /**
   * Contains a list of element keys considered unseen, even if their time stamp is older than
   * {@link #seenAllBefore}.
   * 
   * FIXME: maybe we don't actually need this
   */
  private Set<String> unseenKeys = new HashSet<>();

  public Instant getSeenAllBefore() {
    return seenAllBefore;
  }

  public void setSeenAllBefore(final Instant seenAllSince) {
    this.seenAllBefore = seenAllSince;
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
