package com.upgrade.campside.controller.error;

import com.upgrade.campside.exception.InvalidReservationDateException;
import com.upgrade.campside.exception.ReservationNotFoundException;
import java.util.Date;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Spring handler for some exceptions inside controllers. This class will intercept exceptions
 * thrown by the code inside controllers and handle it gracefully.
 *
 * @author Olavo Holanda
 * @version 0.1
 * @since 0.1
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(InvalidReservationDateException.class)
  public ResponseEntity<Object> handleInvalidDateReservation(InvalidReservationDateException ex) {
    return buildResponseEntity(new HTTPError(ex.getCode(), ex));
  }

  @ExceptionHandler(ReservationNotFoundException.class)
  public ResponseEntity<Object> handleReservationNotFound(ReservationNotFoundException ex) {
    return buildResponseEntity(new HTTPError(ex.getCode(), ex));
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
      HttpHeaders headers, HttpStatus status, WebRequest request) {
    return buildResponseEntity(new HTTPError(HttpStatus.BAD_REQUEST.value(), ex));
  }

  private ResponseEntity<Object> buildResponseEntity(HTTPError error) {
    return ResponseEntity.status(error.getStatusCode()).body(error);
  }
}
