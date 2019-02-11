package com.upgrade.campside.exception;

/**
 * Custom exception for reservation not found.
 * Status code is 404 Not Found.
 *
 * @author Olavo Holanda
 * @version 0.1
 * @since 0.1
 */
public class ReservationNotFoundException extends Exception {

  private final int CODE = 404;
  private static final String MESSAGE = "Reservation with booking id %d not found.";

  public ReservationNotFoundException(Long bookingId) {
    super(String.format(MESSAGE, bookingId));
  }

  public int getCode() {
    return CODE;
  }
}
