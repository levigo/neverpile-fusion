package com.neverpile.fusion.api;

import com.neverpile.fusion.model.seen.SeenUnseenInfo;

public interface SeenUnseenService {
  /**
   * Return the seen/unseen information for a certain context (as indicated by the given key) and a
   * certain principal. Return an empty {@link SeenUnseenInfo} (indicating nothing has evern been
   * seen) if no info has previously been persisted.
   * 
   * @param contextKey the context key
   * @param principalKey the principal key
   * @return the {@link SeenUnseenInfo}
   */
  SeenUnseenInfo get(String contextKey, String principalKey);

  /**
   * Persist (save or update) the seen/unseen information for a certain context (as indicated by the
   * given key) and a certain principal.
   * 
   * @param contextKey the context key
   * @param principalKey the principal key
   * @param info
   */
  void save(String contextKey, String principalKey, SeenUnseenInfo info);

  /**
   * Delete the seen/unseen information for a certain context (as indicated by the given key) and a
   * certain principal.
   * 
   * @param contextKey the context key
   * @param principalKey the principal key
   * @param info
   */
  void delete(String contextKey, String principalKey);
}
