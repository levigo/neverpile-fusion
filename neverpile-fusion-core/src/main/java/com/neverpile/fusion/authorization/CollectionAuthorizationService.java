package com.neverpile.fusion.authorization;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.neverpile.common.authorization.api.Action;
import com.neverpile.common.authorization.api.AuthorizationContext;
import com.neverpile.common.authorization.api.AuthorizationContextContributor;
import com.neverpile.common.authorization.api.AuthorizationService;
import com.neverpile.common.authorization.api.Permission;
import com.neverpile.common.authorization.policy.impl.CompositeAuthorizationContext;
import com.neverpile.common.authorization.policy.impl.PrefixAuthorizationContext;
import com.neverpile.fusion.model.Collection;

/**
 * The CollectionAuthorizationService handles authorization of actions on collections.
 */
@Component
public class CollectionAuthorizationService {
  public static final String COLLECTION_RESOURCE = "collection";

  @Autowired
  AuthorizationService authorizationService;

  @Autowired(required = false)
  List<AuthorizationContextContributor<Collection>> contextContributors;

  /**
   * Authorize the given action on the given collection. The permissions are obtained by retrieving
   * the permissions for the resource <code>collection</code> with an authorization context
   * providing access to the collection itself.
   * 
   * @param collection the collection
   * @param action the action to be authorized
   * @return <code>true</code> if the action shall be permitted, <code>false</code> otherwise.
   */
  public boolean authorizeCollectionAction(final Collection collection, final Action action) {
    return authorizationService.isAccessAllowed("collection", Collections.singleton(action),
        constructAuthorizationContext(collection));
  }

  /**
   * Get the client permissions for the given collection. The client permissions are obtained by
   * retrieving the permissions for the resource <code>collection</code> with an authorization
   * context providing access to the collection itself.
   * 
   * @param collection the collection
   * @return the client permissions as a list of {@link Permission} objects
   */
  public List<Permission> getClientPermissions(final Collection collection) {
    return authorizationService.getPermissions("collection", constructAuthorizationContext(collection));
  }

  private AuthorizationContext constructAuthorizationContext(final Collection collection) {
    CompositeAuthorizationContext authContext = new CompositeAuthorizationContext();

    if (null != contextContributors)
      contextContributors.forEach(cc -> authContext.subContext(cc.contributeAuthorizationContext(collection)));

    return new PrefixAuthorizationContext(COLLECTION_RESOURCE, authContext);
  }
}
