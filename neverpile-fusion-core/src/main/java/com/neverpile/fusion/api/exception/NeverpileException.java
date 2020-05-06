package com.neverpile.fusion.api.exception;

/**
 * A common base exception for all domain specific exceptions.
 */
public class NeverpileException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public NeverpileException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public NeverpileException(final String message) {
    super(message);
  }
}
