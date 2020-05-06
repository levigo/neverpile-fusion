package com.neverpile.fusion.impl;

import java.util.UUID;

import com.neverpile.fusion.api.CollectionIdStrategy;

/**
 * An implementation of a CollectionIdStrategy based on globally unique identifiers (UUIDs).
 */
public class UuidCollectionIdGenerationStrategy implements CollectionIdStrategy {
  private static final String UUID_PATTERN = "\\p{XDigit}{8}-(\\p{XDigit}{4}-){3}\\p{XDigit}{12}";

  @Override
  public String creatcollectionId() {
    return UUID.randomUUID().toString();
  }

  @Override
  public boolean validateCollectionId(final String id) {
    return id.matches(UUID_PATTERN);
  }

  @Override
  public String createElementId() {
    return UUID.randomUUID().toString();
  }

  @Override
  public boolean validateElementId(final String id) {
    return id.matches(UUID_PATTERN);
  }
}
