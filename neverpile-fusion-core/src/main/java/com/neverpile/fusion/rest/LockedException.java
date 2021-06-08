package com.neverpile.fusion.rest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.LOCKED)
public class LockedException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public LockedException(String message) {
    super(message);
  }
}
