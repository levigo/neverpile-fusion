package com.neverpile.fusion.model.spec;

import org.springframework.http.MediaType;

/**
 * A general artifact which could be, for example, a document, an image or a media stream.
 */
public class Artifact extends Specification {
  /**
   * The URI by which the artifact can be addressed. This may or may not be also a URL. If it is not
   * a URL, further, integration-specific resolution must be performed to turn a URI into a URL
   * suitable for loading the actual artifact data.
   */
  private String contentURI;

  /**
   * The media type of the artifact. Used, e.g. by frontend applications to find a suitable viewer.
   */
  private MediaType mediaType;

  public String getContentURI() {
    return contentURI;
  }

  public void setContentURI(final String contentURI) {
    this.contentURI = contentURI;
  }

  public MediaType getMediaType() {
    return mediaType;
  }

  public void setMediaType(final MediaType mediaType) {
    this.mediaType = mediaType;
  }
}
