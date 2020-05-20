package com.neverpile.fusion.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.NOT_FOUND, reason="Version not found")
public class VersionNotFoundException extends NeverpileException {
  private static final long serialVersionUID = 1L;

  public VersionNotFoundException(final String msg, final String version) {
    super("version: " + version + " : " + msg);
  }
}
