package com.neverpile.fusion.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.neverpile.common.authorization.api.Permission;
import com.neverpile.fusion.api.CollectionService;
import com.neverpile.fusion.authorization.CollectionAuthorizationService;
import com.neverpile.fusion.model.Collection;
import com.neverpile.fusion.rest.exception.NotFoundException;

import io.micrometer.core.annotation.Timed;

/**
 * A REST resource providing view layout services. While certain client implementations may be able
 * to perform view layout generation on their own, this service can be used by clients which cannot.
 */
@RestController
@RequestMapping(path = "/api/v1/collections", produces = {
    MediaType.APPLICATION_JSON_VALUE
})
public class ClientPermissionResource {

  @Autowired
  private CollectionService collectionService;

  @Autowired
  private CollectionAuthorizationService collectionAuthorizationService;

  @GetMapping(value = "{collectionId}/permissions")
  @Timed(description = "get collection permissions", value = "fusion.collection.permissions")
  public List<Permission> layout(@PathVariable("collectionId") final String collectionId) {
    Collection collection = collectionService.getCurrent(collectionId).orElseThrow(
        () -> new NotFoundException("Collection not found"));

    return collectionAuthorizationService.getClientPermissions(collection);
  }

}
