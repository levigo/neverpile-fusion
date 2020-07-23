package com.neverpile.fusion.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.Principal;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.neverpile.common.authorization.api.CoreActions;
import com.neverpile.fusion.api.CollectionIdStrategy;
import com.neverpile.fusion.api.CollectionService;
import com.neverpile.fusion.api.CollectionTypeService;
import com.neverpile.fusion.api.exception.PermissionDeniedException;
import com.neverpile.fusion.authorization.CollectionAuthorizationService;
import com.neverpile.fusion.model.Collection;
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
@RequestMapping(path = "/api/v1/collections", produces = MediaType.APPLICATION_JSON_VALUE)
public class CollectionResource {

  @Autowired
  private CollectionService collectionService;
  
  @Autowired
  private CollectionTypeService collectionTypeService;

  @Autowired
  private CollectionIdStrategy idGenerationStrategy;

  @Autowired
  private CollectionAuthorizationService collectionAuthorizationService;

  @PreSignedUrlEnabled
  @GetMapping(value = "{collectionID}")
  @Timed(description = "get collection (current version)", extraTags = {
      "operation", "retrieve", "target", "collection"
  }, value = "fusion.collection.get")
  public Collection getCurrent(@PathVariable("collectionID") final String collectionId) {
    Collection collection = collectionService.getCurrent(collectionId).orElseThrow(
        () -> new NotFoundException("Collection not found"));

    if (!collectionAuthorizationService.authorizeCollectionAction(collection, CoreActions.GET))
      throw new PermissionDeniedException();

    return collection;
  }

  @PreSignedUrlEnabled
  @GetMapping(value = "{collectionID}/history/{versionTimestamp}")
  @Timed(description = "get collection (version specified by timestamp)", extraTags = {
      "operation", "retrieve", "target", "collection"
  }, value = "fusion.collection.get-version")
  public Collection getVersion(@PathVariable("collectionID") final String collectionId,
      @PathVariable("versionTimestamp") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final Instant versionTimestamp) {
    Collection collection = collectionService.getVersion(collectionId, versionTimestamp).orElseThrow(
        () -> new NotFoundException("Collection not found"));

    if (!collectionAuthorizationService.authorizeCollectionAction(collection, CoreActions.GET))
      throw new PermissionDeniedException();

    return collection;
  }

  @PreSignedUrlEnabled
  @GetMapping(value = "{collectionID}/history")
  @Timed(description = "get version list", extraTags = {
      "operation", "retrieve", "target", "collection-version-list"
  }, value = "fusion.collection.get-version-list")
  public List<Date> getVersionList(@PathVariable("collectionID") final String collectionId) {
    if (!collectionAuthorizationService.authorizeCollectionAction(getCurrent(collectionId), CoreActions.GET))
      throw new PermissionDeniedException();

    return collectionService.getVersions(collectionId).stream().map(i -> Date.from(i)).collect(Collectors.toList());
  }

  @PreSignedUrlEnabled
  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  @Timed(description = "save collection", extraTags = {
      "operation", "store", "target", "collection"
  }, value = "fusion.collection.create")
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<Collection> create(@RequestBody final Collection collection, final Principal principal) throws URISyntaxException {
    if (collection.getId() != null) {
      if (!idGenerationStrategy.validateCollectionId(collection.getId()))
        throw new NotAcceptableException("Invalid id: " + collection.getId());
    } else
      collection.setId(idGenerationStrategy.creatcollectionId());

    collection.setCreatedBy(principal.getName());

    beforeSave(collection);

    if (!collectionAuthorizationService.authorizeCollectionAction(collection, CoreActions.CREATE))
      throw new PermissionDeniedException();

    Collection saved = collectionService.save(collection);

    return ResponseEntity.created(new URI(collection.getId())) //
        .lastModified(saved.getDateModified()) //
        .body(saved);
  }

  private void beforeSave(final Collection collection) {
    // validate collection
    if(collection.getTypeId() == null)
      throw new NotAcceptableException("Type id is missing");
    if(collectionTypeService.get(collection.getTypeId()).isEmpty())
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

      if (null == e.getDateModified())
        e.setDateModified(now);
    });
  }

  @PreSignedUrlEnabled
  @PutMapping(value = "{collectionID}", consumes = MediaType.APPLICATION_JSON_VALUE)
  @Timed(description = "update collection", extraTags = {
      "operation", "store", "target", "collection"
  }, value = "fusion.collection.save")
  @ResponseStatus(HttpStatus.CREATED)
  public Collection createOrUpdate(@PathVariable("collectionID") final String collectionId,
      @RequestBody final Collection collection) {
    // collection id in JSON must match the one in the path or be null
    if (collection.getId() != null) {
      if (!Objects.equals(collectionId, collection.getId()))
        throw new NotAcceptableException("Collection id mismatch: " + collection.getId());
    } else
      collection.setId(collectionId);

    Optional<Collection> existing = collectionService.getCurrent(collectionId);

    beforeSave(collection);

    if (!collectionAuthorizationService.authorizeCollectionAction(collection, existing.isPresent() ? CoreActions.UPDATE : CoreActions.CREATE))
      throw new PermissionDeniedException();

    return collectionService.save(collection);
  }
}
