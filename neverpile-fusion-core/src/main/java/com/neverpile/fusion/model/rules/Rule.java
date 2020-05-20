package com.neverpile.fusion.model.rules;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.neverpile.fusion.model.rules.javascript.JavascriptRule;

/**
 * Rules are used to generate view layouts from collection elements. There is currently just one concrete
 * rule implementation based on JavaScript: {@link JavascriptRule}.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = JavascriptRule.class, name = "javascript"),
})
public abstract class Rule {
  private String name;

  public String getName() {
    return name;
  }

  public void setName(final String description) {
    this.name = description;
  }
}
