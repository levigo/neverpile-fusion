package com.neverpile.fusion.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.NOT_ACCEPTABLE, reason="Permission denied")
public class NotAcceptableException extends NeverpileException {
  private static final long serialVersionUID = 1L;

  public NotAcceptableException(final String msg) {
    super(msg);
  }
}
