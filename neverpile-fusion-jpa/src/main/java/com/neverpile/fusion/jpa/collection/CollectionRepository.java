package com.neverpile.fusion.jpa.collection;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 * A spring CRUD repository for collections.
 */
public interface CollectionRepository extends CrudRepository<CollectionEntity, IdAndVersionTimestamp> {

  Optional<CollectionEntity> findByIdAndVersionTimestampAfterAndVersionTimestampBefore(String id,
      Instant versionTimestampStart, Instant versionTimestampEnd);

  @Query("select f from CollectionEntity f where f.id = ?1 and f.versionTimestamp = "
      + "(select max(m.versionTimestamp) from CollectionEntity m where m.id = ?1 group by m.id)")
  Optional<CollectionEntity> findCurrent(String id);

  @Query("select f.versionTimestamp from CollectionEntity f where f.id = ?1 order by versionTimestamp asc")
  List<Instant> findVersions(String id);

  @Query("select max(m.versionTimestamp) from CollectionEntity m where m.id = ?1 group by m.id")
  Optional<Instant> findCurrentVersion(String id);
}
