package com.neverpile.fusion.jpa.collection;

import java.time.Instant;

import com.neverpile.fusion.model.VersionMetadata;


/**
 * A projection of just the fields that go into the {@link VersionMetadata}.
 */
public interface VersionMetadataProjection {

  Instant getVersionTimestamp();

  String getTypeId();

  String getCreatedBy();

}