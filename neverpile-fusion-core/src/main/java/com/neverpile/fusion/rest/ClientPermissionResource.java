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
 * A resource providing client permissions pertaining to a collection. See
 * {@link CollectionAuthorizationService#getClientPermissions(Collection)}. For details see also the
 * OpenAPI specification in
 * /neverpile-fusion-core/src/main/resources/com/neverpile/fusion/fusion-core.yaml.
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
