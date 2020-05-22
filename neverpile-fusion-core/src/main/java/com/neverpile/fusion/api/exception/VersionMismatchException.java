package com.neverpile.fusion.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.CONFLICT, reason="Version mismatch")
public class VersionMismatchException extends NeverpileException {
  private static final long serialVersionUID = 1L;

  public VersionMismatchException(final String msg,final String expectedVersion,final String actualVersion){
    super("expectedVersion: " + expectedVersion + " actualVersion: " + actualVersion + " : " + msg);
  }
}
