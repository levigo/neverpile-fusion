package com.neverpile.fusion.rest;

import java.net.URISyntaxException;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.neverpile.fusion.api.SeenUnseenService;
import com.neverpile.fusion.model.Collection;
import com.neverpile.fusion.model.seen.SeenUnseenInfo;

import io.micrometer.core.annotation.Timed;

/**
 * A REST resource providing access to seen/unseen info. It is backed by a {@link SeenUnseenService}
 * implementation. For details see the OpenAPI specification in
 * /neverpile-fusion-core/src/main/resources/com/neverpile/fusion/fusion-core.yaml.
 */
@RestController
@RequestMapping(path = "/api/v1/seen", produces = MediaType.APPLICATION_JSON_VALUE)
@ConditionalOnBean(SeenUnseenService.class)
public class SeenUnseenResource {

  @Autowired
  private SeenUnseenService seenUnseenService;

  @GetMapping(value = "{contextKey}")
  @Timed(description = "get seen/unseen info (current principal)", extraTags = {
      "operation", "retrieve", "target", "seen/unseen"
  }, value = "fusion.seen.get")
  public SeenUnseenInfo get(@PathVariable("contextKey") final String contextKey, final Principal principal) {
    return seenUnseenService.get(contextKey, principal.getName());
  }

  @PutMapping(value = "{contextKey}", consumes = MediaType.APPLICATION_JSON_VALUE)
  @Timed(description = "persist seen/unseen", extraTags = {
      "operation", "persist", "target", "seen/unseen"
  }, value = "fusion.seen.persist")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public ResponseEntity<Collection> createOrUpdate(@PathVariable("contextKey") final String contextKey,
      @RequestBody final SeenUnseenInfo info, final Principal principal) throws URISyntaxException {
    seenUnseenService.save(contextKey, principal.getName(), info);

    return ResponseEntity.noContent().build();
  }

  @DeleteMapping(value = "{contextKey}")
  @Timed(description = "delete seen/unseen", extraTags = {
      "operation", "delete", "target", "seen/unseen"
  }, value = "fusion.seen.delete")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public ResponseEntity<Collection> delete(@PathVariable("contextKey") final String contextKey,
      final Principal principal) throws URISyntaxException {
    seenUnseenService.delete(contextKey, principal.getName());

    return ResponseEntity.noContent().build();
  }
}