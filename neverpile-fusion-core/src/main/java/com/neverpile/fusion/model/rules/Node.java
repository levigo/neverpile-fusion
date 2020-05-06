package com.neverpile.fusion.model.rules;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;


/**
 * A node of the view tree.
 * 
 * @param <N> the type of node
 */
@JsonInclude(Include.NON_EMPTY)
public class Node<N extends Node<N>> {

  /**
   * Used-defined properties generated for this node. Properties can be anything and thus don't
   * follow a particular schema.
   */
  private Map<String, Object> properties = new HashMap<>();

  /**
   * A map of visualizations generated for this node. The key is the visualization type (e.g.
   * <code>html</code>), the value is the string representation of the visualization.
   */
  private Map<String, String> visualization = new HashMap<>();

  public Node() {
    super();
  }

  public Map<String, Object> getProperties() {
    return properties;
  }

  public void setProperties(final Map<String, Object> properties) {
    this.properties = Objects.requireNonNull(properties, "properties");
  }

  public Map<String, String> getVisualization() {
    return visualization;
  }

  public void setVisualization(final Map<String, String> visualization) {
    this.visualization = visualization;
  }

  @SuppressWarnings("unchecked")
  public N withProperty(final String name, final Object value) {
    getProperties().put(name, value);
    return (N) this;
  }

  @SuppressWarnings("unchecked")
  public N withVisualization(final String type, final String representation) {
    getVisualization().put(type, representation);
    return (N) this;
  }
}