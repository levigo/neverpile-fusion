package com.neverpile.fusion.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.FORBIDDEN, reason="Permission denied")
public class PermissionDeniedException extends NeverpileException {
  private static final long serialVersionUID = 1L;

  public PermissionDeniedException() {
    super("Permission denied");
  }
}
