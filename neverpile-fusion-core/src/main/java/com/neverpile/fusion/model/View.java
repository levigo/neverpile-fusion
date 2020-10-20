package com.neverpile.fusion.model;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;

import com.neverpile.fusion.model.rules.Rule;

/**
 * A definition of a collection view.
 */
public class View {
  /**
   * The name of the view. Usually displayed to the user when presenting a choice of views.
   */
  private String name;

  /**
   * A list of rules used generate the view based on the collection's elements. The rules are
   * executed/called once per element.
   */
  private List<Rule> elementRules = new ArrayList<>();

  /**
   * A list of rules used finalize the view. They can be used for applications like sorting, pruning
   * the tree etc.
   */
  private List<Rule> treeRules = new ArrayList<>();

  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public List<Rule> getElementRules() {
    return elementRules;
  }

  public List<Rule> getTreeRules() {
    return treeRules;
  }

  public void setElementRules(final List<Rule> elementRules) {
    this.elementRules = requireNonNull(elementRules, "elementRules");
  }

  public void setTreeRules(final List<Rule> treeRules) {
    this.treeRules = requireNonNull(treeRules, "treeRules");
  }
}
