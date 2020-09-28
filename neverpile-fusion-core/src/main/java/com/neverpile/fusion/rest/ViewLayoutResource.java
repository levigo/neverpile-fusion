package com.neverpile.fusion.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.neverpile.fusion.api.CollectionTypeService;
import com.neverpile.fusion.model.Collection;
import com.neverpile.fusion.model.CollectionType;
import com.neverpile.fusion.model.rules.ViewLayout;
import com.neverpile.fusion.model.rules.javascript.JavascriptViewLayoutEngine;
import com.neverpile.fusion.rest.exception.NotFoundException;

import io.micrometer.core.annotation.Timed;

/**
 * A REST resource providing view layout services. While certain client implementations may be able
 * to perform view layout generation on their own, this service can be used by clients which cannot.
 */
@RestController
@RequestMapping(path = "/api/v1/layout", produces = {
    MediaType.APPLICATION_JSON_VALUE
})
public class ViewLayoutResource {

  @Autowired
  private CollectionTypeService collectionTypeService;

  @Autowired
  private JavascriptViewLayoutEngine engine;

  @PostMapping(value = "{typeId}", consumes = MediaType.APPLICATION_JSON_VALUE)
  @Timed(description = "get collection type by id", extraTags = {
      "operation", "layout", "target", "collection"
  }, value = "fusion.collection.layout")
  public List<ViewLayout> layout(@PathVariable("typeId") final String id, @RequestBody final Collection collection) {
    CollectionType type = collectionTypeService.get(id).orElseThrow(
        () -> new NotFoundException("Collection type not found: " + id));

    return engine.layoutTree(collection, type);
  }

}
