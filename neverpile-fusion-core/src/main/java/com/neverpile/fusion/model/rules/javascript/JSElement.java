package com.neverpile.fusion.model.rules.javascript;

import java.util.Date;
import java.util.List;

import com.neverpile.fusion.model.spec.Specification;

/**
 * This is a bridge class used to represent a collection element within the JavaScript rule execution context. 
 */
public class JSElement {
  private String id;
  
  private Date dateCreated;

  private Date dateModified;

  private List<String> tags;
  
  private Specification specification;
  
  private Object metadata;
  
  public String getId() {
    return id;
  }

  public void setId(final String id) {
    this.id = id;
  }

  public Date getDateCreated() {
    return dateCreated;
  }

  public void setDateCreated(final Date creationDate) {
    this.dateCreated = creationDate;
  }

  public Date getDateModified() {
    return dateModified;
  }

  public void setDateModified(final Date modificationDate) {
    this.dateModified = modificationDate;
  }

  public List<String> getTags() {
    return tags;
  }

  public void setTags(final List<String> tags) {
    this.tags = tags;
  }

  public Specification getSpecification() {
    return specification;
  }

  public void setSpecification(final Specification specification) {
    this.specification = specification;
  }

  public Object getMetadata() {
    return metadata;
  }

  public void setMetadata(final Object metadata) {
    this.metadata = metadata;
  }
}
