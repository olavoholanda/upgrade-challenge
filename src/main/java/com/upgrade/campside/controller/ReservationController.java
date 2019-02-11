package com.upgrade.campside.controller;

import com.upgrade.campside.controller.dto.ReservationDTO;
import com.upgrade.campside.controller.dto.custom.NewReservation;
import com.upgrade.campside.controller.dto.custom.UpdateReservation;
import com.upgrade.campside.domain.model.Reservation;
import com.upgrade.campside.exception.InvalidReservationDateException;
import com.upgrade.campside.exception.ReservationNotFoundException;
import com.upgrade.campside.service.ReservationService;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Rest endpoint for reservation resource, including CRUD and query operations.
 *
 * @author Olavo Holanda
 * @version 0.1
 * @since 0.1
 */
@RestController
@RequestMapping("/api/reservation")
public class ReservationController {

  private final ReservationService service;

  /**
   * Class constructor with AutoWired dependencies injection.
   */
  @Autowired
  public ReservationController(ReservationService service) {
    this.service = service;
  }

  /**
   * Creates a new <code>Reservation</code> based on the person email, full name, arrival and
   * departure dates. By default the reservation has a CONFIRMED status.
   *
   * Constraints: - Reservations can be for a 3 day max; - Arrival date must be at least one day
   * from now; - Arrival date must be up to one month from now; - Reservations can not overlap;
   *
   * @param reservation the <code>NewReservation</code> holding the reservation info.
   * @return a new reservation.
   * @throws InvalidReservationDateException the dates constraints fail
   */
  @RequestMapping(method = RequestMethod.POST)
  public ReservationDTO createReservation(@Valid @RequestBody NewReservation reservation)
      throws InvalidReservationDateException {

    String email = reservation.getOwner().getEmail();
    String fullName = reservation.getOwner().getFullName();
    LocalDate arrival = reservation.getArrivalDate();
    LocalDate departure = reservation.getDepartureDate();

    Reservation newReservation = this.service
        .createReservation(email, fullName, arrival, departure);

    return this.parseReservation(newReservation);
  }

  /**
   * Updates a specific <code>Reservation</code> dates based on its booking id. If the new dates
   * satisfy all constraints, the reservation will be update with success.
   *
   * Constraints: - Reservations can be for a 3 day max; - Arrival date must be at least one day
   * from now; - Arrival date must be up to one month from now; - Reservations can not overlap;
   *
   * @param reservation the <code>UpdateReservation</code> holding the reservation info.
   * @return an updated reservation.
   * @throws InvalidReservationDateException the dates constraints fail
   * @throws ReservationNotFoundException reservation not found
   */
  @RequestMapping(method = RequestMethod.PUT)
  public ReservationDTO updateReservation(@Valid @RequestBody UpdateReservation reservation)
      throws ReservationNotFoundException, InvalidReservationDateException {

    Long bookingId = reservation.getBookingId();
    LocalDate newArrival = reservation.getArrivalDate();
    LocalDate newDeparture = reservation.getDepartureDate();

    Reservation updatedReservation = this.service
        .updateReservationDates(bookingId, newArrival, newDeparture);

    return this.parseReservation(updatedReservation);
  }

  /**
   * Cancels a specific <code>Reservation</code> based on its booking id.
   *
   * @param bookingId the <code>String</code> holding the person's full name.
   * @return an updated reservation.
   * @throws ReservationNotFoundException reservation not found
   */
  @RequestMapping(path = "/cancel/{bookingId}", method = RequestMethod.PUT)
  public ReservationDTO cancelReservation(@PathVariable("bookingId") Long bookingId)
      throws ReservationNotFoundException {

    Reservation updatedReservation = this.service
        .cancelReservation(bookingId);
    return this.parseReservation(updatedReservation);
  }

  /**
   * Queries the availability of the Campside. yyyy-MM-dd
   *
   * @param startDate an <code>Optional<LocalDate></></code> with the start date.
   * @param endDate an <code>Optional<LocalDate></></code> with the end date.
   * @return a list of reservations during this time frame
   */
  @RequestMapping(path = "/availability", method = RequestMethod.GET)
  public List<ReservationDTO> getAvailability(
      @RequestParam(value = "start_date", required = false)
      @DateTimeFormat(iso = ISO.DATE) LocalDate startDate,
      @RequestParam(value = "end_date", required = false)
      @DateTimeFormat(iso = ISO.DATE) LocalDate endDate) {

    Optional<LocalDate> optStart = Optional.ofNullable(startDate);
    Optional<LocalDate> optEnd = Optional.ofNullable(endDate);

    if (!optStart.isPresent() && !optEnd.isPresent()) {
      //case both are not present, default time is one month from now
      startDate = LocalDate.now();
      endDate = LocalDate.now().plusMonths(1);
    } else if (optStart.isPresent() && !optEnd.isPresent()) {
      //case only start date is present, default time is one month from start date
      endDate = startDate.plusMonths(1);
    } else if (!optStart.isPresent()) {
      //case only end date is present, default time is one month before end date
      startDate = endDate.minusMonths(1);
    } // if both dates are present, do nothing just use them

    List<Reservation> reservationList = this.service
        .getAvailability(startDate, endDate);

    return reservationList.stream().map(this::parseReservation).collect(Collectors.toList());
  }

  /**
   * Parses an entity model Reservation to a ReservationDTO.
   *
   * @param reservation a <code>Reservation</code> instance.
   * @return the reservation data transfer object.
   */
  private ReservationDTO parseReservation(Reservation reservation) {
    ReservationDTO dto = new ReservationDTO();
    dto.buildFromEntity(reservation);
    return dto;
  }
}
