package com.neverpile.fusion.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.Principal;
import java.time.Instant;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.neverpile.common.authorization.api.CoreActions;
import com.neverpile.common.locking.LockService;
import com.neverpile.common.locking.RequestLockingService;
import com.neverpile.common.locking.RequestLockingService.RequestScopedLock;
import com.neverpile.fusion.api.CollectionIdStrategy;
import com.neverpile.fusion.api.CollectionService;
import com.neverpile.fusion.api.CollectionTypeService;
import com.neverpile.fusion.api.exception.PermissionDeniedException;
import com.neverpile.fusion.authorization.CollectionAuthorizationService;
import com.neverpile.fusion.configuration.ApplicationConfiguration;
import com.neverpile.fusion.model.Collection;
import com.neverpile.fusion.model.VersionMetadata;
import com.neverpile.fusion.rest.exception.NotAcceptableException;
import com.neverpile.fusion.rest.exception.NotFoundException;
import com.neverpile.urlcrypto.PreSignedUrlEnabled;

import io.micrometer.core.annotation.Timed;

/**
 * A REST resource providing access to collections. It is backed by a {@link CollectionService}
 * implementation. For details see the OpenAPI specification in
 * /neverpile-fusion-core/src/main/resources/com/neverpile/fusion/fusion-core.yaml.
 */
@RestController
@RequestMapping(
    path = "/api/v1/collections",
    produces = MediaType.APPLICATION_JSON_VALUE)
public class CollectionResource {

  private static final long VERSION_AGGREGATION_WINDOW = TimeUnit.SECONDS.toMillis(30);
  @Autowired
  private CollectionService collectionService;

  @Autowired
  private CollectionTypeService collectionTypeService;

  @Autowired
  private CollectionIdStrategy idGenerationStrategy;

  @Autowired
  private CollectionAuthorizationService collectionAuthorizationService;

  @Autowired
  private RequestLockingService lockService;

  @Autowired
  private ApplicationConfiguration configuration;

  @PreSignedUrlEnabled
  @GetMapping("{collectionID}")
  @Timed(
      description = "get collection (current version)",
      extraTags = {
          "operation", "retrieve", "target", "collection"
      },
      value = "fusion.collection.get")
  public ResponseEntity<Collection> getCurrent(@PathVariable("collectionID") final String collectionId) {
    Collection collection = currentVersion(collectionId);

    return ResponseEntity.ok() //
        .header(LockService.LOCK_SCOPE_HEADER, createLockScope(collectionId)) //
        .body(collection);
  }

  /**
   * Return the current version of a collection.
   * 
   * @param collectionId the collection id
   * @return the current version of the collection
   * @throws NotFoundException if the collection does not exist
   * @throws PermissionDeniedException if the access is denied
   */
  private Collection currentVersion(final String collectionId) {
    Collection collection = collectionService.getCurrent(collectionId).orElseThrow(
        () -> new NotFoundException("Collection not found"));

    if (!collectionAuthorizationService.authorizeCollectionAction(collection, CoreActions.GET))
      throw new PermissionDeniedException();

    return collection;
  }

  @PreSignedUrlEnabled
  @GetMapping("{collectionID}/history/{versionTimestamp}")
  @Timed(
      description = "get collection (version specified by timestamp)",
      extraTags = {
          "operation", "retrieve", "target", "collection"
      },
      value = "fusion.collection.get-version")
  public ResponseEntity<Collection> getVersion(@PathVariable("collectionID") final String collectionId,
      @PathVariable("versionTimestamp") @DateTimeFormat(
          iso = DateTimeFormat.ISO.DATE_TIME) final Instant versionTimestamp) {
    Collection collection = collectionService.getVersion(collectionId, versionTimestamp).orElseThrow(
        () -> new NotFoundException("Collection not found"));

    if (!collectionAuthorizationService.authorizeCollectionAction(collection, CoreActions.GET))
      throw new PermissionDeniedException();

    return ResponseEntity.ok() //
        .header(LockService.LOCK_SCOPE_HEADER, createLockScope(collectionId)) //
        .body(collection);
  }

  @PreSignedUrlEnabled
  @GetMapping("{collectionID}/history")
  @Timed(
      description = "get version list",
      extraTags = {
          "operation", "retrieve", "target", "collection-version-list"
      },
      value = "fusion.collection.get-version-list")
  public List<Date> getVersionList(@PathVariable("collectionID") final String collectionId) {
    if (!collectionAuthorizationService.authorizeCollectionAction(currentVersion(collectionId), CoreActions.GET))
      throw new PermissionDeniedException();

    return collectionService.getVersions(collectionId).stream().map(i -> Date.from(i)).collect(Collectors.toList());
  }

  @PreSignedUrlEnabled
  @GetMapping("{collectionID}/historyWithMetadata")
  @Timed(
      description = "get history with metadata",
      extraTags = {
          "operation", "retrieve", "target", "collection-version-list"
      },
      value = "fusion.collection.get-version-list-with-metadata")
  public List<VersionMetadata> getVersionListWithMetadata(@PathVariable("collectionID") final String collectionId,
      @RequestParam(
          name = "groupRelatedVersions",
          defaultValue = "false") final boolean groupRelatedVersions) {
    if (!collectionAuthorizationService.authorizeCollectionAction(currentVersion(collectionId), CoreActions.GET))
      throw new PermissionDeniedException();

    List<VersionMetadata> versionsWithMetadata = collectionService.getVersionsWithMetadata(collectionId);

    if (groupRelatedVersions) {
      VersionMetadata prev = null;
      for (Iterator<VersionMetadata> i = versionsWithMetadata.iterator(); i.hasNext();) {
        VersionMetadata v = i.next();

        // aggregate two versions, if they are
        if (prev != null
            // created by same user
            && Objects.equals(prev.getCreatedBy(), v.getCreatedBy())
            // have same type
            && Objects.equals(prev.getTypeId(), v.getTypeId())
            // one version timestamp list is empty (should not happen)
            && (prev.getVersionTimestamps().isEmpty() //
                || v.getVersionTimestamps().isEmpty()
                // or difference between timestamps within aggregation window
                || Math.abs(v.getVersionTimestamps().get(0).toEpochMilli() - prev.getVersionTimestamps().get(
                    prev.getVersionTimestamps().size() - 1).toEpochMilli()) < VERSION_AGGREGATION_WINDOW //
            )) {
          // add version to previous one
          prev.getVersionTimestamps().addAll(v.getVersionTimestamps());
          i.remove();
        } else {
          prev = v;
        }
      }
    }

    return versionsWithMetadata;
  }

  @PreSignedUrlEnabled
  @PostMapping(
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @Timed(
      description = "save collection",
      extraTags = {
          "operation", "store", "target", "collection"
      },
      value = "fusion.collection.create")
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<Collection> create(@RequestBody final Collection collection, final Principal principal)
      throws URISyntaxException {
    if (collection.getId() != null) {
      if (!idGenerationStrategy.validateCollectionId(collection.getId()))
        throw new NotAcceptableException("Invalid id: " + collection.getId());
    } else
      collection.setId(idGenerationStrategy.creatcollectionId());

    beforeSave(collection, principal, Optional.empty());

    if (!collectionAuthorizationService.authorizeCollectionAction(collection, CoreActions.CREATE))
      throw new PermissionDeniedException();

    Collection saved = collectionService.save(collection);

    return ResponseEntity.created(new URI(collection.getId())) //
        .lastModified(saved.getDateModified()) //
        .body(saved);
  }

  private void beforeSave(final Collection collection, final Principal principal, Optional<Collection> existing) {
    // validate collection
    if (collection.getTypeId() == null)
      throw new NotAcceptableException("Type id is missing");
    if (collectionTypeService.get(collection.getTypeId()).isEmpty())
      throw new NotAcceptableException("No such collection type");

    Instant now = Instant.now();

    // set creation/modification date
    if (null == collection.getDateCreated())
      collection.setDateCreated(now);

    if (null == collection.getDateModified())
      collection.setDateModified(now);

    collection.getElements().forEach(e -> {
      // generate/validate element IDs
      if (e.getId() == null)
        e.setId(idGenerationStrategy.createElementId());
      else if (!idGenerationStrategy.validateElementId(e.getId()))
        throw new NotAcceptableException("Invalid id: " + collection.getId());

      // set creation/modification date
      if (null == e.getDateCreated())
        e.setDateCreated(now);

      boolean isUpdate = existing // find element in existing collection
          .flatMap(c -> c.getElements().stream().filter(xe -> Objects.equals(xe.getId(), e.getId())).findFirst())
          // is it changed?
          .map(xe -> !Objects.equals(xe, e))
          // if there is no existing element, we assume unchanged
          .orElse(false);

      if (null == e.getDateModified() || isUpdate)
        e.setDateModified(now);
    });

    // set created by
    collection.setCreatedBy(principal.getName());
  }

  @PreSignedUrlEnabled
  @PutMapping(
      value = "{collectionID}",
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @Timed(
      description = "update collection",
      extraTags = {
          "operation", "store", "target", "collection"
      },
      value = "fusion.collection.save")
  @ResponseStatus(HttpStatus.CREATED)
  public Collection createOrUpdate(@PathVariable("collectionID") final String collectionId,
      @RequestBody final Collection collection, final Principal principal, @RequestHeader(
          name = LockService.LOCK_TOKEN_HEADER,
          required = false) String lockToken) {
    // collection id in JSON must match the one in the path or be null
    if (collection.getId() != null) {
      if (!Objects.equals(collectionId, collection.getId()))
        throw new NotAcceptableException("Collection id mismatch: " + collection.getId());
    } else
      collection.setId(collectionId);

    Optional<Collection> existing = collectionService.getCurrent(collectionId);

    // lock, but only on updates
    RequestScopedLock lock = existing.isPresent()
        ? lockService.performLocking(createLockScope(collectionId), configuration.getLocking().getMode())
        : () -> {
          // no need to unlock
        };
    try {
      beforeSave(collection, principal, existing);

      if (!collectionAuthorizationService.authorizeCollectionAction(collection,
          existing.isPresent() ? CoreActions.UPDATE : CoreActions.CREATE))
        throw new PermissionDeniedException();

      return collectionService.save(collection);
    } finally {
      lock.releaseIfLocked();
    }
  }

  private String createLockScope(final String collectionId) {
    return "neverpile:collection:" + collectionId;
  }
}
