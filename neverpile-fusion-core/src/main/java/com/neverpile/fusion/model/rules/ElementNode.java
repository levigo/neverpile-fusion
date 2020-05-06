package com.neverpile.fusion.model.rules;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.neverpile.fusion.model.Element;

/**
 * An element node of the view tree. Element nodes refer to elements of the collection.
 */
@JsonInclude(Include.NON_EMPTY)
public class ElementNode extends Node<ElementNode> {
  /**
   * The id of the element this node refers to.
   */
  private String id;
  
  public ElementNode() {
  }
  
  public ElementNode(final Element e) {
    this.id = e.getId();
  }

  public String getId() {
    return id;
  }

  public void setId(final String elementId) {
    this.id = elementId;
  }
}
