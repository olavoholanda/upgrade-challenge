package com.upgrade.campside.controller.dto.custom;

import com.upgrade.campside.controller.dto.ReservationDTO;
import java.time.LocalDate;
import javax.validation.constraints.NotNull;
import lombok.Getter;

/**
 * An Object Representing a update Reservation object, using Bean Validation.
 *
 * @author Olavo Holanda
 * @version 0.1
 * @since 0.1
 */
@Getter
public class UpdateReservation extends ReservationDTO {

  @NotNull
  private Long bookingId;
  @NotNull
  private LocalDate arrivalDate;
  @NotNull
  private LocalDate departureDate;
}
