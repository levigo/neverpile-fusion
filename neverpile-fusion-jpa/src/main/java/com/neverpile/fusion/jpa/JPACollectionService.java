package com.neverpile.fusion.jpa;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.neverpile.fusion.api.CollectionService;
import com.neverpile.fusion.api.exception.VersionMismatchException;
import com.neverpile.fusion.api.exception.VersionNotFoundException;
import com.neverpile.fusion.model.Collection;

/**
 * An implementation of {@link CollectionService} which persists collections to a SQL database via JPA. 
 */
@Component
public class JPACollectionService implements CollectionService {
  private final CollectionRepository repository;
  private final ModelMapper modelMapper;
  private final Clock clock;

  @Autowired
  public JPACollectionService(final CollectionRepository repository, final ModelMapper modelMapper, final Clock clock) {
    this.repository = repository;
    this.modelMapper = modelMapper;
    this.clock = clock;
  }

  @Override
  public Optional<Collection> getCurrent(final String id) {
    return repository.findCurrent(id).map(e -> modelMapper.map(e, Collection.class));
  }

  @Override
  public Optional<Collection> getVersion(final String id, final Instant versionTimestamp) {
    return repository.findByIdAndVersionTimestamp(id, versionTimestamp) //
        .map(e -> modelMapper.map(e, Collection.class));
  }

  @Override
  public List<Instant> getVersions(final String id) {
    return repository.findVersions(id);
  }

  @Override
  @Transactional(isolation = Isolation.READ_COMMITTED)
  public Collection save(final Collection collection) {
    Objects.requireNonNull(collection.getId(), "Collection id");

    Optional<Instant> currentVersion = repository.findCurrentVersion(collection.getId());
    if (collection.getVersionTimestamp() != null) {
      if (currentVersion.isPresent()) {
        // saving new version with version timestamp set to the current one
        Instant currentTimestamp = currentVersion.get();
        if (!currentTimestamp.equals(collection.getVersionTimestamp()))
          throw new VersionMismatchException("Failed to update collection: version is not the current one",
              currentTimestamp.toString(), collection.getVersionTimestamp().toString());
      } else {
        // saving a new collection requires a version timestamp of null
        throw new VersionMismatchException("Failed to save new collection: no existing version", "<null>",
            collection.getVersionTimestamp().toString());
      }
    } else {
      // provided collection does not have a version timestamp
      if (currentVersion.isPresent()) {
        /*
         * FIXME: Saving new version without version checking - we may want to deny that.
         */
      }
    }

    // invent a version time stamp now
    Instant newVersionTimestamp = clock.instant();
    collection.setVersionTimestamp(newVersionTimestamp);
    
    // detect backwards-running clock
    if(currentVersion.isPresent() && !currentVersion.get().isBefore(newVersionTimestamp))
      throw new VersionMismatchException("Detected clock running backwards during save", newVersionTimestamp.toString(),
          currentVersion.get().toString());

    // perform save
    Collection saved = modelMapper.map(repository.save(modelMapper.map(collection, CollectionEntity.class)), Collection.class);

    // Verify version after save, so we don't have to specify isolation SERIALIZABLE
    Instant mostRecentVersionAfterSave = repository.findCurrentVersion(collection.getId()).orElseThrow(
        () -> new VersionNotFoundException("Did not find saved version", newVersionTimestamp.toString()));
    if (!mostRecentVersionAfterSave.equals(newVersionTimestamp))
      throw new VersionMismatchException("Version conflict saving new Version", newVersionTimestamp.toString(),
          mostRecentVersionAfterSave.toString());

    return saved;
  }

}
