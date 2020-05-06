package com.neverpile.fusion.api.exception;

public class VersionNotFoundException extends NeverpileException {
  private static final long serialVersionUID = 1L;

  public VersionNotFoundException(final String msg, final String version) {
    super("version: " + version + " : " + msg);
  }
}
