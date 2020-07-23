package com.neverpile.fusion.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.Principal;
import java.time.Instant;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import com.neverpile.fusion.model.Element;
import com.neverpile.fusion.rest.exception.AlreadyExistsException;
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
@RequestMapping(path = "/api/v1/collections/{collectionID}", produces = MediaType.APPLICATION_JSON_VALUE)
public class CollectionElementResource {

  @Autowired
  private CollectionService collectionService;

  @Autowired
  private CollectionTypeService collectionTypeService;

  @Autowired
  private CollectionIdStrategy idGenerationStrategy;

  @Autowired
  private CollectionAuthorizationService collectionAuthorizationService;

  @PreSignedUrlEnabled
  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, path = "/elements")
  @Timed(description = "save collection", extraTags = {
      "operation", "store", "target", "collection"
  }, value = "fusion.collection.create")
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<Element> createElement(@PathVariable("collectionID") final String collectionId,
      @RequestBody final Element element, final Principal principal) throws URISyntaxException {
    if (element.getId() != null) {
      if (!idGenerationStrategy.validateCollectionId(element.getId()))
        throw new NotAcceptableException("Invalid id: " + element.getId());
    } else
      element.setId(idGenerationStrategy.creatcollectionId());

    Collection collection = collectionService.getCurrent(collectionId).orElseThrow(
        () -> new NotFoundException("Collection not found"));

    if (collection.getElements().stream().anyMatch(e -> element.getId().equals(e.getId())))
      throw new AlreadyExistsException("Duplicate id: " + element.getId());

    element.setDateCreated(Instant.now());
    element.setDateModified(Instant.now());

    // add the element
    collection.getElements().add(element);

    if (!collectionAuthorizationService.authorizeCollectionAction(collection, CoreActions.UPDATE))
      throw new PermissionDeniedException();

    Collection saved = doSave(collection);

    Element created = saved.getElements().stream().filter(
        e -> element.getId().equals(e.getId())).findFirst().orElseThrow();

    return ResponseEntity.created(new URI(collection.getId() + "/elements/" + element.getId())) //
        .lastModified(saved.getDateModified()) //
        .body(created);
  }

  private Collection doSave(final Collection collection) {
    collection.setDateModified(Instant.now());
    return collectionService.save(collection);
  }

  @PreSignedUrlEnabled
  @GetMapping(value = "elements/{elementID}")
  @Timed(description = "get collection element", extraTags = {
      "operation", "retrieve", "target", "collection-element"
  }, value = "fusion.collection.get")
  public Element getElement(@PathVariable("collectionID") final String collectionId,
      @PathVariable("elementID") final String elementId) {
    Collection collection = collectionService.getCurrent(collectionId).orElseThrow(
        () -> new NotFoundException("Collection not found"));

    if (!collectionAuthorizationService.authorizeCollectionAction(collection, CoreActions.GET))
      throw new PermissionDeniedException();

    return collection.getElements().stream().filter(e -> elementId.equals(e.getId())).findFirst().orElseThrow(
        () -> new NotFoundException("Element not found"));
  }

  @PreSignedUrlEnabled
  @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, path = "elements/{elementID}")
  @Timed(description = "save collection", extraTags = {
      "operation", "store", "target", "collection"
  }, value = "fusion.collection.create")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<Element> updateElement(@PathVariable("collectionID") final String collectionId,
      @PathVariable("elementID") final String elementId, @RequestBody final Element element, final Principal principal)
      throws URISyntaxException {
    if (element.getId() != null) {
      if (!Objects.equals(element.getId(), elementId))
        throw new NotAcceptableException("Id inside element must match id in path or be null");
    } else
      element.setId(elementId);

    Collection collection = collectionService.getCurrent(collectionId).orElseThrow(
        () -> new NotFoundException("Collection not found"));

    Element existingElement = collection.getElements().stream() //
        .filter(e -> element.getId().equals(e.getId())).findFirst() //
        .orElseThrow(() -> new NotFoundException("Element not found"));

    element.setDateCreated(existingElement.getDateCreated());
    element.setDateModified(Instant.now());

    // update the element
    collection.getElements().replaceAll(e -> e == existingElement ? element : e);

    if (!collectionAuthorizationService.authorizeCollectionAction(collection, CoreActions.UPDATE))
      throw new PermissionDeniedException();

    Element updated = doSave(collection).getElements().stream().filter(
        e -> element.getId().equals(e.getId())).findFirst().orElseThrow();

    return ResponseEntity.ok() //
        .lastModified(updated.getDateModified()) //
        .body(updated);
  }

  @PreSignedUrlEnabled
  @DeleteMapping(path = "elements/{elementID}")
  @Timed(description = "save collection", extraTags = {
      "operation", "store", "target", "collection"
  }, value = "fusion.collection.create")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public ResponseEntity<?> deleteElement(@PathVariable("collectionID") final String collectionId,
      @PathVariable("elementID") final String elementId, final Principal principal)
          throws URISyntaxException {
    Collection collection = collectionService.getCurrent(collectionId).orElseThrow(
        () -> new NotFoundException("Collection not found"));
    
    // update the element
    if(!collection.getElements().removeIf(e -> e.getId().equals(elementId)))
      throw new NotFoundException("Element not found");
    
    if (!collectionAuthorizationService.authorizeCollectionAction(collection, CoreActions.UPDATE))
      throw new PermissionDeniedException();
    
    Collection saved = collectionService.save(collection);
    
    return ResponseEntity.noContent() //
        .lastModified(saved.getDateModified()) //
        .build();
  }
}
