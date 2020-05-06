package com.neverpile.fusion.model.rules;

import com.neverpile.fusion.model.rules.javascript.JavascriptRule;

/**
 * Rules are used to generate view layouts from collection elements. There is currently just one concrete
 * rule implementation based on JavaScript: {@link JavascriptRule}.
 */
public abstract class Rule {
  private String name;

  public String getName() {
    return name;
  }

  public void setName(final String description) {
    this.name = description;
  }
}
