package com.neverpile.fusion.model.rules;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class CollectionLayout {
  /**
   * The type's unique id.
   */
  private String collectionTypeId;
  
  /**
   * A map of title-visualizations generated for this collection. The key is the visualization type (e.g.
   * <code>html</code>), the value is the string representation of the visualization.
   */
  private Map<String, String> titleVisualization = new HashMap<>();

  /**
   * A list of view layouts 
   */
  private Map<String, ViewLayout> viewLayouts = new HashMap<>();

  public String getCollectionTypeId() {
    return collectionTypeId;
  }

  public void setCollectionTypeId(final String collectionTypeId) {
    this.collectionTypeId = collectionTypeId;
  }

  public Map<String, String> getTitleVisualization() {
    return titleVisualization;
  }

  public void setTitleVisualization(final Map<String, String> titleVisualization) {
    this.titleVisualization = titleVisualization;
  }

  public Map<String, ViewLayout> getViewLayouts() {
    return viewLayouts;
  }

  public void setViewLayouts(final Map<String, ViewLayout> viewLayouts) {
    this.viewLayouts = viewLayouts;
  }
}
