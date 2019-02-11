package com.upgrade.campside.controller.error;

import java.time.LocalDateTime;
import lombok.Getter;

/**
 * Error object for all HTTP messages.
 *
 * @author Olavo Holanda
 * @version 0.1
 * @since 0.1
 */
@Getter
class HTTPError {

  private int statusCode;
  private LocalDateTime timestamp;
  private String message;

  /**
   * Default constructor for exceptions.
   *
   * @param status the HTTP status code
   * @param ex the Exception with a localized message
   */
  HTTPError(int status, Throwable ex) {
    this.timestamp = LocalDateTime.now();
    this.statusCode = status;
    this.message = ex.getLocalizedMessage();
  }
}
