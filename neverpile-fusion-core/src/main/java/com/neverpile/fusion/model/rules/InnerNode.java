package com.neverpile.fusion.model.rules;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.neverpile.fusion.model.Element;

/**
 * An inner node of the view tree.
 */
@JsonInclude(Include.NON_EMPTY)
public class InnerNode extends Node<InnerNode> {
  private String name;

  /**
   * The element (leaf-) nodes for this inner node.
   */
  private List<ElementNode> elements = new ArrayList<>();

  /**
   * The child nodes of this node.
   */
  private List<InnerNode> children = new ArrayList<>();

  public InnerNode() {
  }

  public InnerNode(final String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public List<ElementNode> getElements() {
    return elements;
  }

  public void setElements(final List<ElementNode> elements) {
    this.elements = elements;
  }

  public List<InnerNode> getChildren() {
    return children;
  }

  public void setChildren(final List<InnerNode> children) {
    this.children = Objects.requireNonNull(children, "children");
  }

  public InnerNode createChild(final String p) {
    Optional<InnerNode> child = children.stream().filter(m -> Objects.equals(p, m.getName())).findFirst();
    if (child.isPresent()) {
      return child.get();
    } else {
      InnerNode n = new InnerNode(p);
      children.add(n);
      return n;
    }
  }

  public InnerNode findChild(final String p) {
    return children.stream().filter(m -> Objects.equals(p, m.getName())).findFirst().orElse(null);
  }

  public InnerNode createPath(final String... nodePath) {
    InnerNode n = this;
    for (String p : nodePath) {
      n = n.createChild(p);
    }
    return n;
  }

  public InnerNode findNode(final String... nodePath) {
    InnerNode n = this;
    for (String p : nodePath) {
      n = n.findChild(p);
      if (null == n)
        return null;
    }
    return n;
  }

  public ElementNode withElement(final Element e) {
    ElementNode elementEntry = new ElementNode(e);
    this.elements.add(elementEntry);
    return elementEntry;
  }
}