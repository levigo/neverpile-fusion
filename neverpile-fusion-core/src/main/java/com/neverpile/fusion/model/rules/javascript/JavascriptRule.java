package com.neverpile.fusion.model.rules.javascript;

import com.neverpile.fusion.model.rules.Rule;

/**
 * A rule implementation based on JavaScript code. JavaScript rules are executed in a rule execution
 * environment where they have access to the relevant domain objects and utility functions.
 * 
 * FIXME: document the rule execution environment/API.
 */
public class JavascriptRule extends Rule {
  /**
   * The actual JavaScript code for the rule.
   */
  private String scriptCode;

  public String getScriptCode() {
    return scriptCode;
  }

  public void setScriptCode(final String scriptCode) {
    this.scriptCode = scriptCode;
  }
}
