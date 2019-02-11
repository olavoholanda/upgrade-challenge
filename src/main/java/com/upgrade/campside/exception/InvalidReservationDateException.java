package com.upgrade.campside.exception;

/**
 * Custom exception for constraint date violation when try to book a reservation.
 * Status code is 400 Bad Request.
 *
 * @author Olavo Holanda
 * @version 0.1
 * @since 0.1
 */
public class InvalidReservationDateException extends Exception {

  private final int CODE = 400;

  public InvalidReservationDateException(String message) {
    super(message);
  }

  public int getCode() {
    return CODE;
  }
}
