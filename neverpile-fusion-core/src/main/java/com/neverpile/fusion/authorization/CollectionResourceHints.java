package com.neverpile.fusion.authorization;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

import com.neverpile.common.authorization.api.HintRegistrations;
import com.neverpile.common.authorization.policy.ResourceHints;

/**
 * Hints pertaining to authorization decisions on collections. Used by frontend permission editors. 
 */
@Component
@ResourceHints
public class CollectionResourceHints implements HintRegistrations {
  @Override
  public List<Hint> getHints() {
    return Arrays.asList( //
        new Hint(CollectionAuthorizationService.COLLECTION_RESOURCE + ".type", "the collection type"), //
        new Hint(CollectionAuthorizationService.COLLECTION_RESOURCE + ".state", "the collection state"), //
        new Hint(CollectionAuthorizationService.COLLECTION_RESOURCE + ".dateCreated", "the date and time when the collection was created"), //
        new Hint(CollectionAuthorizationService.COLLECTION_RESOURCE + ".dateModified", "the date and time when the collection was modified"), //
        new Hint(CollectionAuthorizationService.COLLECTION_RESOURCE + ".createdBy", "the name of the user who created the collection"), //
        new Hint(CollectionAuthorizationService.COLLECTION_RESOURCE + ".metadata...", "the collection's metadata aas a JSON path") //
    );
  }
}