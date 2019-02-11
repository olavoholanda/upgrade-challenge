package com.upgrade.campside.service;

import com.upgrade.campside.domain.model.Person;
import com.upgrade.campside.domain.model.Reservation;
import com.upgrade.campside.domain.model.ReservationStatus;
import com.upgrade.campside.domain.repository.ReservationRepository;
import com.upgrade.campside.exception.InvalidReservationDateException;
import com.upgrade.campside.exception.ReservationNotFoundException;
import java.time.LocalDate;
import java.util.List;
import javax.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * The <code>ReservationService</code> class is responsible for operations regarding reservations.
 * For queries methods look for <code>ReservationQueryService</code>. As a service this class uses
 * only its related repository, in this case <code>ReservationRepository</code>, operations on
 * others entities are performed using the related services.
 *
 * @author Olavo Holanda
 * @version 0.1
 * @since 0.1
 */
@Service
public class ReservationService {

  private final ReservationRepository repository;

  /**
   * Class constructor with AutoWired dependencies injection.
   */
  @Autowired
  public ReservationService(ReservationRepository repository) {
    this.repository = repository;
  }

  /**
   * Creates a new <code>Reservation</code> based on the person email, full name, arrival and
   * departure dates. By default the reservation has a CONFIRMED status. Attention, this operation
   * uses synchronized to avoid overlap concurrent reservation dates.
   *
   * Constraints: - Reservations can be for a 3 day max; - Arrival date must be at least one day
   * from now; - Arrival date must be up to one month from now; - Reservations can not overlap;
   *
   * @param email the <code>String</code> holding the person's email.
   * @param fullName the <code>String</code> holding the person's full name.
   * @param arrival the <code>LocalDate</code> arrival.
   * @param departure the <code>LocalDate</code> departure.
   * @return a new, persisted, reservation.
   * @throws InvalidReservationDateException the dates constraints fail
   */
  @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.SERIALIZABLE)
  public synchronized Reservation createReservation(String email, String fullName, LocalDate arrival,
      LocalDate departure) throws InvalidReservationDateException {
    validateReservationDates(arrival, departure, null);

    Person person = new Person(email, fullName);
    Reservation reservation = new Reservation(arrival, departure, person,
        ReservationStatus.CONFIRMED);
    return repository.save(reservation);
  }

  /**
   * Updates a specific <code>Reservation</code> dates based on its booking id. If the new dates
   * satisfy all constraints, the reservation will be update with success. Attention, this operation
   * uses synchronized to avoid overlap concurrent reservation dates.
   *
   * Constraints: - Reservations can be for a 3 day max; - Arrival date must be at least one day
   * from now; - Arrival date must be up to one month from now; - Reservations can not overlap;
   *
   * @param bookingId the <code>Long</code> holding the booking id.
   * @param newArrival the <code>LocalDate</code> arrival.
   * @param newDeparture the <code>LocalDate</code> departure.
   * @return an updated reservation.
   * @throws InvalidReservationDateException the dates constraints fail
   * @throws ReservationNotFoundException reservation not found
   */
  @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.SERIALIZABLE)
  public synchronized Reservation updateReservationDates(Long bookingId, LocalDate newArrival,
      LocalDate newDeparture) throws InvalidReservationDateException, ReservationNotFoundException {

    validateReservationDates(newArrival, newDeparture, bookingId);

    Reservation current = this.getReservationById(bookingId);
    current.setArrivalDate(newArrival);
    current.setDepartureDate(newDeparture);
    return repository.save(current);
  }

  /**
   * Cancels a specific <code>Reservation</code> based on its booking id.
   *
   * @param bookingId the <code>Long</code> holding the booking id.
   * @return an updated reservation.
   * @throws ReservationNotFoundException reservation not found
   */
  public Reservation cancelReservation(Long bookingId) throws ReservationNotFoundException {
    Reservation current = this.getReservationById(bookingId);
    current.setStatus(ReservationStatus.CANCELED);
    return repository.save(current);
  }

  /**
   * Queries the Campside availability, it will return a list of reservation in the desired time
   * frame.
   *
   * @param startDate the <code>LocalDate</code> with start search.
   * @param endDate the <code>LocalDate</code> with end search.
   * @return a <code>List<Reservation><code> during the time frame.
   */
  public List<Reservation> getAvailability(LocalDate startDate, LocalDate endDate) {
    return repository.getReservationsInPeriod(startDate, endDate);
  }

  /**
   * Validates reservation create/update dates constraints.
   *
   * @param arrival the <code>LocalDate</code> arrival.
   * @param departure the <code>LocalDate</code> departure.
   * @param bookingId the <code>Long</code> with reservation booking id, optional.
   * @throws InvalidReservationDateException the dates constraints fail
   */
  private void validateReservationDates(LocalDate arrival, LocalDate departure, Long bookingId)
      throws InvalidReservationDateException {
    LocalDate now = LocalDate.now();
    if (now.plusDays(1).isAfter(arrival)) {
      throw new InvalidReservationDateException(
          "Too late for this reservation. The campside can be reserved at least one day before arrival.");
    }

    if (now.plusMonths(1).isBefore(arrival)) {
      throw new InvalidReservationDateException(
          "Too soon for this reservation. The campside can be reserved up to one month in advance.");
    }

    if (arrival.plusDays(3).isBefore(departure)) {
      throw new InvalidReservationDateException(
          "Max days exceeded for this reservation. The campside can be reserved for max 3 days.");
    }

    if (isCampsideReserved(arrival, departure, bookingId)) {
      throw new InvalidReservationDateException(
          "Already reserved. The campside is not available during this requested time.");
    }
  }

  /**
   * Checks if the Campside is reserved between the desired dates. If bookingId is present
   * then it will exclude that particular reservations. This is expected if the user is trying
   * to modify its reservation date.
   *
   * @param arrival the <code>LocalDate</code> arrival.
   * @param departure the <code>LocalDate</code> departure.
   * @param bookingId the <code>Long</code> with reservation booking id, optional.
   * @return true if it is reserved false otherwise
   */
  private boolean isCampsideReserved(LocalDate arrival, LocalDate departure, Long bookingId) {
    if (bookingId == null) {
      return repository.checkReservationOverlaps(arrival, departure);
    }
    return repository.checkReservationOverlapsExceptOwn(arrival, departure, bookingId);
  }

  /**
   * Retrieves a reservation by its booking ID. Throws ReservationNotFoundException for an invalid
   * booking ID.
   *
   * @param bookingId the <code>Long</code> bookingID.
   * @return the desired reservation
   * @throws ReservationNotFoundException reservation not found
   */
  private Reservation getReservationById(Long bookingId) throws ReservationNotFoundException {
    try {
      return repository.getOne(bookingId);
    } catch (EntityNotFoundException ex) {
      throw new ReservationNotFoundException(bookingId);
    }
  }
}
