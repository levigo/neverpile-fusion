package com.neverpile.fusion.model.rules;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A layout for a single view.
 */
@JsonInclude(Include.NON_NULL)
public class ViewLayout {
  /**
   * The name of the view to which this layout belongs.
   */
  private String view;

  /**
   * The root node of the view structure tree.
   */
  private Node structureTree;

  /**
   * The error that was generated during layout generation (if any). If this is not
   * <code>null</code>, the {@link #structureTree} may be incomplete or empty.
   */
  @JsonProperty(required = false)
  private String error;

  public ViewLayout() {
  }

  public ViewLayout(final String viewName, final Node root) {
    this.view = viewName;
    this.structureTree = root;
  }

  public ViewLayout(final String viewName, final String error) {
    this.view = viewName;
    this.error = error;
  }

  public String getView() {
    return view;
  }

  public void setView(final String viewName) {
    this.view = viewName;
  }

  public Node getStructureTree() {
    return structureTree;
  }

  public void setStructureTree(final Node root) {
    this.structureTree = root;
  }
}
