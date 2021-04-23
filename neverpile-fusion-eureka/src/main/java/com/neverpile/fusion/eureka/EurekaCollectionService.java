package com.neverpile.fusion.eureka;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neverpile.eureka.client.content.ContentElementFacet;
import com.neverpile.eureka.client.core.ClientException;
import com.neverpile.eureka.client.core.ContentElement;
import com.neverpile.eureka.client.core.Document;
import com.neverpile.eureka.client.core.DocumentService.ContentElementResponse;
import com.neverpile.eureka.client.core.NeverpileEurekaClient;
import com.neverpile.eureka.client.core.NotFoundException;
import com.neverpile.eureka.client.metadata.MetadataFacetBuilder;
import com.neverpile.fusion.api.CollectionService;
import com.neverpile.fusion.api.exception.NeverpileException;
import com.neverpile.fusion.api.exception.VersionMismatchException;
import com.neverpile.fusion.model.Collection;
import com.neverpile.fusion.model.VersionMetadata;

/**
 * An implementation of {@link CollectionService} which persists collections to neverpile eureka
 * repository. The repository must be configured with multi-versioning enabled.
 */
@Component
public class EurekaCollectionService implements CollectionService {
  private static final String COLLECTION_ROLE_NAME = "neverpile-fusion-collection";

  private static final Logger LOGGER = LoggerFactory.getLogger(EurekaCollectionService.class);

  private final NeverpileEurekaClient client;
  private final ObjectMapper objectMapper;
  private final Clock clock;

  @Autowired
  public EurekaCollectionService(final ObjectMapper objectMapper, final NeverpileEurekaClient client,
      final Clock clock) {
    this.objectMapper = objectMapper;
    this.client = client;
    this.clock = clock;
  }

  @Override
  public Optional<Collection> getCurrent(final String id) {
    try {
      ContentElementResponse contentElement = client.documentService().queryContent(id).withRole(
          COLLECTION_ROLE_NAME).getOnly();

      Collection collection = objectMapper.readValue(contentElement.getContent(), Collection.class);

      // the version timestamp isn't persisted but derived from the eureka version
      collection.setVersionTimestamp(contentElement.getVersionTimestamp());

      return Optional.of(collection);
    } catch (JsonMappingException | JsonParseException e) {
      LOGGER.error("Failed to unmarshal collection", e);
      throw new NeverpileException("Failed to unmarshal collection", e);
    } catch (ClientException e) {
      // Eureka signals 400 BAD REQUEST for malformed document or element IDs - we treat them as
      // simply nonexistent
      if (e.getCode() == 400)
        return Optional.empty();
      throw e;
    } catch (NotFoundException e) {
      return Optional.empty();
    } catch (IOException e) {
      LOGGER.error("Failed to retrieve collection", e);
      return Optional.empty();
    }
  }

  @Override
  public Optional<Collection> getVersion(final String id, final Instant versionTimestamp) {
    try {
      ContentElementResponse contentElement = client.documentService().queryContent(id, versionTimestamp).withRole(
          COLLECTION_ROLE_NAME).getOnly();
      Collection collection = objectMapper.readValue(contentElement.getContent(), Collection.class);

      // the version timestamp isn't persisted but derived from the eureka version
      collection.setVersionTimestamp(contentElement.getVersionTimestamp());

      return Optional.of(collection);
    } catch (JsonMappingException | JsonParseException e) {
      LOGGER.error("Failed to unmarshal collection", e);
      throw new NeverpileException("Failed to unmarshal collection", e);
    } catch (NotFoundException e) {
      return Optional.empty();
    } catch (IOException e) {
      LOGGER.error("Failed to retrieve collection version", e);
      return Optional.empty();
    }
  }

  @Override
  public List<Instant> getVersions(final String id) {
    return client.documentService().getVersions(id);
  }

  @Override
  public List<VersionMetadata> getVersionsWithMetadata(final String id) {
    return getVersions(id).stream() 
        // retrieve metadata for all versions - this is going to be slow...
        .map(ts -> getVersion(id, ts) //
            .map(v -> new VersionMetadata(v.getVersionTimestamp(), v.getTypeId(), v.getCreatedBy())).orElse(null)) //
        .filter(Objects::nonNull) //
        .collect(Collectors.toList());
  }

  @Override
  public Collection save(final Collection collection) {
    Objects.requireNonNull(collection.getId(), "Collection id");

    try {
      Instant vts = collection.getVersionTimestamp();

      // don't persist the version timestamp - it is identical to the document version timestamp
      // which we do not know yet.
      collection.setVersionTimestamp(null);
      byte[] serialized = objectMapper.writeValueAsBytes(collection);

      // restore timestamp
      collection.setVersionTimestamp(vts);

      // does the document already exist?
      Optional<Document> currentVersion = client.documentService().getDocument(collection.getId());
      if (currentVersion.isPresent()) {
        ContentElement addedOrUpdated = addOrUpdateForExistingDocument(collection, serialized, currentVersion.get());

        collection.setVersionTimestamp(addedOrUpdated.getVersionTimestamp());
      } else {
        if (null != vts)
          throw new VersionMismatchException("Saving a new collection requires a null version timestamp", "null",
              vts.toString());

        Document doc = createNewDocumentWithCollection(collection, serialized);

        collection.setVersionTimestamp(doc.getVersionTimestamp());
      }

      return collection;
    } catch (JsonProcessingException e) {
      throw new NeverpileException("Can't marshal to JSON", e);
    }
  }

  private Document createNewDocumentWithCollection(final Collection collection, final byte[] serialized) {
    // @formatter:off
    Document doc = client.documentService()
      .newDocument() //
        .id(collection.getId()) //
        .facet(MetadataFacetBuilder.metadata())
          // add the collection's metadata as document metadata
          .jsonMetadata(COLLECTION_ROLE_NAME)
          .content(collection.getMetadata())
          .attach()
        .contentElement()
          .content(serialized)
          .role(COLLECTION_ROLE_NAME)
          .mediaType(MediaType.APPLICATION_JSON_VALUE)
          .fileName("neverpile-fusion-collection.json")
          .attach()
        .save();
    // @formatter:on
    return doc;
  }

  private ContentElement addOrUpdateForExistingDocument(final Collection collection, final byte[] serialized,
      final Document currentVersion) {
    Optional<ContentElement> existingCollectionElement = currentVersion.facet(ContentElementFacet.class).flatMap(
        l -> l.stream().filter(ce -> ce.getRole().equals(COLLECTION_ROLE_NAME)).findFirst());

    // version check
    if (collection.getVersionTimestamp() != null) {
      // saving new version with version timestamp set to the current one
      Instant currentTimestamp = currentVersion.getVersionTimestamp();
      if (!currentTimestamp.equals(collection.getVersionTimestamp()))
        throw new VersionMismatchException("Failed to update collection: version is not the current one",
            currentTimestamp.toString(), collection.getVersionTimestamp().toString());

      // detect backwards-running clock
      Instant now = clock.instant();
      if (now.isBefore(currentTimestamp))
        throw new VersionMismatchException("Detected clock running backwards during save", currentTimestamp.toString(),
            now.toString());
    }

    ContentElement addedOrUpdated;
    if (existingCollectionElement.isPresent()) {
      addedOrUpdated = client.documentService().updateContentElement(currentVersion.getDocumentId(),
          existingCollectionElement.get().getId(), serialized, MediaType.APPLICATION_JSON_VALUE);
    } else {
      // should not happen, but we support it anyway
      addedOrUpdated = client.documentService().addContentElement(currentVersion.getDocumentId(),
          new ByteArrayInputStream(serialized), MediaType.APPLICATION_JSON_VALUE, COLLECTION_ROLE_NAME,
          "collection.json");
    }
    return addedOrUpdated;
  }
}
