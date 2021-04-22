package com.neverpile.fusion.model.spec;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.springframework.http.MediaType;

import com.fasterxml.jackson.databind.JsonNode;


/**
 * A collection element consisting of paged media (e.g. PDFs, scanned images, etc.) which can be
 * arbitrarily composed from any number of sources to form a virtual document.
 */
public class CompositePaged extends Specification {
  /**
   * A PageSequence references a set of pages from a single part document.
   */
  public static class PageSequence {
    /**
     * The media type of the part document. Used, e.g. by frontend applications to find a suitable
     * viewer.
     */
    private MediaType mediaType;

    /**
     * The URI by which the part document can be addressed. This may or may not be also a URL. If it
     * is not a URL, further, integration-specific resolution must be performed to turn a URI into a
     * URL suitable for loading the actual document data.
     */
    private String contentURI;

    /**
     * The pages selected from the source document for inclusion into the composite paged. Indices
     * are zero-based, i.e. the first page is index 0. As a special case, if this property is null
     * (but <em>not</em> the empty array!) in implies <em>all</em> pages of the source to be
     * included.
     */
    private int[] pageIndices;

    /**
     * Annnotation data pertaining to a single part document.
     * 
     * @see CompositePaged#annotationData
     */
    private JsonNode annotationData;

    /**
     * Render settings data on a per-page-sequence basis.
     */
    private JsonNode renderSettingsData;

    public MediaType getMediaType() {
      return mediaType;
    }

    public void setMediaType(final MediaType mediaType) {
      this.mediaType = mediaType;
    }

    public String getContentURI() {
      return contentURI;
    }

    public void setContentURI(final String contentURI) {
      this.contentURI = contentURI;
    }

    public int[] getPageIndices() {
      return pageIndices;
    }

    public void setPageIndices(final int[] pageIndices) {
      this.pageIndices = pageIndices;
    }

    public JsonNode getAnnotationData() {
      return annotationData;
    }

    public void setAnnotationData(final JsonNode annotationData) {
      this.annotationData = annotationData;
    }

    public JsonNode getRenderSettingsData() {
      return renderSettingsData;
    }

    public void setRenderSettingsData(final JsonNode renderSettingsData) {
      this.renderSettingsData = renderSettingsData;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((annotationData == null) ? 0 : annotationData.hashCode());
      result = prime * result + ((contentURI == null) ? 0 : contentURI.hashCode());
      result = prime * result + ((mediaType == null) ? 0 : mediaType.hashCode());
      result = prime * result + Arrays.hashCode(pageIndices);
      result = prime * result + ((renderSettingsData == null) ? 0 : renderSettingsData.hashCode());
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
      PageSequence other = (PageSequence) obj;
      return Objects.equals(annotationData, other.annotationData) //
          && Objects.equals(contentURI, other.contentURI) //
          && Objects.equals(mediaType, other.mediaType) //
          && Arrays.equals(pageIndices, other.pageIndices) //
          && Objects.equals(renderSettingsData, other.renderSettingsData);
    }
  }

  private List<PageSequence> pageSequences = new ArrayList<>();

  /**
   * Annotations are markings, textual notes or other forms of supplementary information added to a
   * paged document that can be applied by a user without changing the underlying document
   * information. Anotations can be transported in various forms, e.g. as binary streams, XML, JSON
   * etc.
   * <p>
   * For CompositePageds, annotation data can be managed as one stream/object set for the whole
   * element or per individual page sequence. Only one should be used for a particular element.
   */
  private JsonNode annotationData;

  public List<PageSequence> getPageSequences() {
    return pageSequences;
  }

  public void setPageSequences(final List<PageSequence> pageSequences) {
    this.pageSequences = pageSequences;
  }

  public JsonNode getAnnotationData() {
    return annotationData;
  }

  public void setAnnotationData(final JsonNode annotationData) {
    this.annotationData = annotationData;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((annotationData == null) ? 0 : annotationData.hashCode());
    result = prime * result + ((pageSequences == null) ? 0 : pageSequences.hashCode());
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
    CompositePaged other = (CompositePaged) obj;
    return Objects.equals(annotationData, other.annotationData) //
        && Objects.equals(pageSequences, other.pageSequences);
  }
}
