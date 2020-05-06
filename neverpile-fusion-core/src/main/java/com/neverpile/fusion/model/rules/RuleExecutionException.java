package com.neverpile.fusion.model.rules;

import com.neverpile.fusion.api.exception.NeverpileException;

/**
 * An exception type thrown during rule execution.
 */
public class RuleExecutionException extends NeverpileException {
  private static final long serialVersionUID = 1L;
  private final String ruleName;


  public RuleExecutionException(final String ruleName, final String message) {
    super(message);
    this.ruleName = ruleName;
  }


  public RuleExecutionException(final String ruleName, final String message, final Throwable cause) {
    super(message, cause);
    this.ruleName = ruleName;
  }

  public String getRuleName() {
    return ruleName;
  }
}
