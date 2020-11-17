package com.neverpile.fusion.model.rules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.neverpile.fusion.model.Element;


/**
 * A node of the view tree.
 */
@JsonInclude(Include.NON_EMPTY)
public class Node {

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

  protected String name;

  /**
   * The id of the element this node refers to.
   */
  private String elementId;

  /**
   * The child nodes of this node.
   */
  private List<Node> children = new ArrayList<>();

  /**
   * Whether the node shall be initially expanded when opening the view.
   */
  private boolean initiallyExpanded;

  /**
   * String containing a native JavaScript function, executed on dragover.
   */
  private String onDragoverExecutable;

  /**
   * String containing a native JavaScript function, executed on drop.
   */
  private String onDropExecutable;

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

  public Node withProperty(final String name, final Object value) {
    getProperties().put(name, value);
    return this;
  }

  public Node withVisualization(final String type, final String representation) {
    getVisualization().put(type, representation);
    return this;
  }

  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public List<Node> getChildren() {
    return children;
  }

  public void setChildren(final List<Node> children) {
    this.children = Objects.requireNonNull(children, "children");
  }

  public Node createChild(final String name) {
    Optional<Node> child = children.stream().filter(m -> Objects.equals(name, m.getName())).findFirst();
    if (child.isPresent()) {
      return child.get();
    } else {
      Node n = new Node();
      n.setName(name);
      children.add(n);
      return n;
    }
  }

  public Node findChild(final String p) {
    return children.stream().filter(m -> Objects.equals(p, m.getName())).findFirst().orElse(null);
  }

  public Node createPath(final String... nodePath) {
    Node n = this;
    for (String p : nodePath) {
      n = n.createChild(p);
    }
    return n;
  }

  public Node findNode(final String... nodePath) {
    Node n = this;
    for (String p : nodePath) {
      n = n.findChild(p);
      if (null == n)
        return null;
    }
    return n;
  }

  public Node withElement(final Element e) {
    setElementId(e.getId());
    return this;
  }

  public Node createElementNode(final Element e) {
    return createChild(e.getId()).withElement(e);
  }

  public String getElementId() {
    return elementId;
  }

  public void setElementId(final String elementId) {
    this.elementId = elementId;
  }

  public Node initiallyCollapsed() {
    this.setInitiallyExpanded(false);
    return this;
  }

  public Node initiallyExpanded() {
    this.setInitiallyExpanded(true);
    return this;
  }

  public boolean isInitiallyExpanded() {
    return initiallyExpanded;
  }

  public void setInitiallyExpanded(final boolean initiallyExpanded) {
    this.initiallyExpanded = initiallyExpanded;
  }

  public String getOnDragoverExecutable() {
    return this.onDragoverExecutable;
  }

  public String getOnDropExecutable() {
    return this.onDropExecutable;
  }

  /**
   * The function (defined in parameter 'executable') will be executed when the native Dragover Event is fired.
   * The given executable will be called with the parameter 'event', which is a reference to the JavaScript native DragEvent.
   *
   * @param executable A native JavaScript function.
   * @return This node instance.
   */
  @SuppressWarnings("unchecked")
  public Node onDragover(final String executable) {
    this.onDragoverExecutable = executable;
    return this;
  }

  /**
   * The function (defined in parameter 'executable') will be executed when the native Drop Event is fired.
   * Whether or not content may be dropped on this Node can be controlled with the 'onDragover' function.
   * The given executable will be called with the parameter 'event', which is a reference to the JavaScript native DragEvent.
   *
   * @param executable A native JavaScript function.
   * @return This node instance.
   */
  @SuppressWarnings("unchecked")
  public Node onDrop(final String executable) {
    this.onDropExecutable = executable;
    return this;
  }
}
