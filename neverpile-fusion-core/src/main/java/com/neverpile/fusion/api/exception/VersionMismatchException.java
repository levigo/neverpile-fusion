package com.neverpile.fusion.api.exception;

public class VersionMismatchException extends NeverpileException {
  private static final long serialVersionUID = 1L;

  public VersionMismatchException(final String msg,final String expectedVersion,final String actualVersion){
    super("expectedVersion: " + expectedVersion + " actualVersion: " + actualVersion + " : " + msg);
  }
}
