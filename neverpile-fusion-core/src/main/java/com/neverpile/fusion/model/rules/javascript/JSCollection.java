package com.neverpile.fusion.model.rules.javascript;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This is a bridge class used to represent a collection within the JavaScript rule execution context. 
 */
public class JSCollection {
  private String id;
  
  private Date versionTimestamp;

  private String type;
  
  private List<JSElement> elements = new ArrayList<>();
  
  private Date dateCreated;
  
  private Date dateModified;
  
  private String createdBy;
  
  private Object metadata;
  
  public enum State {
    Active,
    Closed,
    MarkedForDeletion
  }
  
  private State state = State.Active;
  
  public String getId() {
    return id;
  }

  public void setId(final String id) {
    this.id = id;
  }

  public Date getVersionTimestamp() {
    return versionTimestamp;
  }

  public void setVersionTimestamp(final Date modificationDate) {
    this.versionTimestamp = modificationDate;
  }

  public String getType() {
    return type;
  }

  public void setType(final String type) {
    this.type = type;
  }
  
  public State getState() {
    return state;
  }
  
  public void setState(final State state) {
    this.state = state;
  }

  public List<JSElement> getElements() {
    return elements;
  }

  public void setElements(final List<JSElement> elements) {
    this.elements = elements;
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
  
  public void setDateModified(final Date dateModified) {
    this.dateModified = dateModified;
  }

  public String getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(final String createdBy) {
    this.createdBy = createdBy;
  }

  public Object getMetadata() {
    return metadata;
  }

  public void setMetadata(final Object metadata) {
    this.metadata = metadata;
  }
}
