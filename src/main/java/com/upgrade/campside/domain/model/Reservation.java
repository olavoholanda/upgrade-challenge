package com.upgrade.campside.domain.model;

import java.time.LocalDate;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

/**
 * Reservation is the entity class representing a reservation in the Campside. A reservation will
 * hold information about its arrival date, departure date, time of creation, time of update,
 * reservation owner, its booking id and its status (CONFIRMED, CANCELED).
 *
 * @author Olavo Holanda
 * @version 0.1
 * @since 0.1
 */
@Getter
@Setter
@Entity
public class Reservation {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long bookingId;

  @Column(nullable = false)
  private LocalDate arrivalDate;

  @Column(nullable = false)
  private LocalDate departureDate;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private ReservationStatus status;

  @ManyToOne(cascade = CascadeType.ALL)
  private Person owner;

  /**
   * Protected constructor, please use the one with parameters, that are required for this object.
   */
  protected Reservation() {
  }

  /**
   * <code>Reservation</code> constructor, creates a new reservation instance with the
   * mandatory parameters.
   *
   * @param arrivalDate the <code>LocalDate</code> when the person will arrive
   * @param departureDate the <code>LocalDate</code> when the person will leave
   * @param owner the <code>Person</code> that owns the reservation
   * @param status the <code>ReservationStatus</code> current status of the reservation
   */
  public Reservation(LocalDate arrivalDate, LocalDate departureDate, Person owner, ReservationStatus status) {
    this.arrivalDate = arrivalDate;
    this.departureDate = departureDate;
    this.status = status;
    this.owner = owner;
  }

  @Override
  public String toString() {
    return String.format(
        "Reservation[bookingId=%d, arrival='%s', departure='%s', status='%s', person='%s']",
        bookingId, arrivalDate, departureDate, status, owner);
  }
}
