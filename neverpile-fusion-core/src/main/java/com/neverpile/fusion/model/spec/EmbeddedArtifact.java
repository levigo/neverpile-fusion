package com.neverpile.fusion.model.spec;

import java.util.Objects;

import org.springframework.http.MediaType;

/**
 * A general artifact like {@link Artifact} which has its content embedded in the collection instead
 * of being referenced from an external source. This should generally only be used for small payload
 * sizes up to a few kilobytes.
 */
public class EmbeddedArtifact extends Specification {
  /**
   * The media type of the artifact. Used, e.g. by frontend applications to find a suitable viewer.
   */
  private MediaType mediaType;

  /**
   * The actual artifact content. The content may be, for example, encoded as Base64. This must be
   * reflected in the media type by adding an appropriate parameter, e.g.
   * <code>type/subtype;base64</code>.
   */
  private String content;

  public MediaType getMediaType() {
    return mediaType;
  }

  public void setMediaType(final MediaType mediaType) {
    this.mediaType = mediaType;
  }

  public String getContent() {
    return content;
  }

  public void setContent(final String content) {
    this.content = content;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((content == null) ? 0 : content.hashCode());
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
    EmbeddedArtifact other = (EmbeddedArtifact) obj;
    return Objects.equals(content, other.content) //
        && Objects.equals(mediaType, other.mediaType);
  }
}
