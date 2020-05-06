package com.neverpile.fusion.api;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import com.neverpile.fusion.api.exception.VersionMismatchException;
import com.neverpile.fusion.model.Collection;

/**
 * A CollectionService is used to access a set (repository, database etc.) of collections.
 * Implementations must manage collections in a versioning fashion where updates do not replace an
 * existing state but create a new version of it.
 */
public interface CollectionService {
  /**
   * Get the current version of the collection with the given id.
   * 
   * @param id the collection's id
   * @return the current version or the empty optional if there is no such collection.
   */
  Optional<Collection> getCurrent(String id);

  /**
   * Get a particular version of the collection with the given id.
   * 
   * @param id the collection's id
   * @param versionTimestamp the version's timestamp
   * @return the current version or the empty optional if there is no such collection.
   */
  Optional<Collection> getVersion(String id, Instant versionTimestamp);

  /**
   * Get the version timestamps of all versions of the collection with the given id.
   * 
   * @param id the collection's id
   * @return the list of version timestamps or the empty list, if the collection does not exist
   */
  List<Instant> getVersions(String id);

  /**
   * Save a new collection or a new version thereof. Implementations must guard against lost updates
   * by employing optimistic concurrency control. When saving a new version of an existing
   * collection, the version timestamp of the given version must contain the timestamp of the most
   * recently loaded version. Implementations must reject the update, if the presented timestamp is
   * not the most recent one in the repository.
   * <p>
   * When saving new collections, the version timestamp must be <code>null</code>.
   * 
   * @param newVersion the new collection or version
   * @return the saved collection, with the version timestamp updated accordingly.
   * @throws VersionMismatchException if a version mismatch is detected.
   */
  Collection save(Collection newVersion);
}
