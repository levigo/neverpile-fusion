package com.neverpile.fusion.model.spec;

import java.util.Objects;

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

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((contentURI == null) ? 0 : contentURI.hashCode());
    result = prime * result + ((mediaType == null) ? 0 : mediaType.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Artifact other = (Artifact) obj;
    return Objects.equals(contentURI, other.contentURI) //
        && Objects.equals(mediaType, other.mediaType);
  }
}
