package com.upgrade.campside.controller.dto;

import com.upgrade.campside.domain.model.Reservation;
import java.time.LocalDate;
import lombok.Getter;

/**
 * Data Transfer Object for the Reservation Entity
 *
 * @author Olavo Holanda
 * @version 0.1
 * @since 0.1
 */
@Getter
public class ReservationDTO implements DTOMapper<Reservation> {

  private Long bookingId;
  private LocalDate arrivalDate;
  private LocalDate departureDate;
  private String status;
  private PersonDTO owner;

  @Override
  public String toString() {
    return String.format(
        "ReservationDTO[bookingId=%d, arrival='%s', departure='%s', status='%s', person='%s']",
        bookingId, arrivalDate, departureDate, status, owner);
  }

  /**
   * Fills the DTO with information from the <code>Reservation</code> entity.
   *
   * @param reservation the <code>Reservation</code> entity
   */
  @Override
  public void buildFromEntity(Reservation reservation) {
    this.bookingId = reservation.getBookingId();
    this.arrivalDate = reservation.getArrivalDate();
    this.departureDate = reservation.getDepartureDate();
    this.status = reservation.getStatus().name();

    this.owner = new PersonDTO();
    this.owner.buildFromEntity(reservation.getOwner());
  }
}
