package com.upgrade.campside.controller.dto.custom;

import com.upgrade.campside.controller.dto.PersonDTO;
import com.upgrade.campside.controller.dto.ReservationDTO;
import java.time.LocalDate;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Getter;

/**
 * An Object Representing a new Reservation, using Bean Validation.
 *
 * @author Olavo Holanda
 * @version 0.1
 * @since 0.1
 */
@Getter
public class NewReservation extends ReservationDTO {

  @NotNull
  private LocalDate arrivalDate;
  @NotNull
  private LocalDate departureDate;
  @Valid
  @NotNull
  private PersonDTO owner;
}
