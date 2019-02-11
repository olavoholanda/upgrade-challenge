package com.upgrade.campside.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.upgrade.campside.domain.model.Reservation;
import com.upgrade.campside.domain.model.ReservationStatus;
import com.upgrade.campside.domain.repository.ReservationRepository;
import com.upgrade.campside.exception.InvalidReservationDateException;
import java.time.LocalDate;
import java.util.List;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
public class ReservationServiceTest {

  @Autowired
  private ReservationRepository reservationRepository;

  private ReservationService reservationService;

  @Rule
  public ExpectedException exceptionRule = ExpectedException.none();

  @Before
  public void setUp() {
    reservationService = new ReservationService(reservationRepository);
    reservationRepository.deleteAll();
  }

  @Test
  public void createReservation() throws Exception {
    String name = "John Doe";
    String email = "johndoe@email.com";
    LocalDate arrival = LocalDate.now().plusDays(2);
    LocalDate departure = LocalDate.now().plusDays(3);

    Reservation reservation = reservationService.createReservation(email, name, arrival, departure);

    assertNotNull(reservation.getBookingId());
    assertEquals(reservation.getStatus(), ReservationStatus.CONFIRMED);
  }

  @Test
  public void createReservationInvalidMaxDays() throws Exception {
    exceptionRule.expect(InvalidReservationDateException.class);
    exceptionRule.expectMessage("Max days exceeded for this reservation. The campside can be reserved for max 3 days.");

    String name = "John Doe";
    String email = "johndoe@email.com";
    LocalDate arrival = LocalDate.now().plusDays(4);
    LocalDate departure = LocalDate.now().plusDays(10);

    reservationService.createReservation(email, name, arrival, departure);
  }

  @Test
  public void createReservationInvalidOneDayBefore() throws Exception {
    exceptionRule.expect(InvalidReservationDateException.class);
    exceptionRule.expectMessage("Too late for this reservation. The campside can be reserved at least one day before arrival.");

    String name = "John Doe";
    String email = "johndoe@email.com";
    LocalDate arrival = LocalDate.now();
    LocalDate departure = LocalDate.now().plusDays(1);

    reservationService.createReservation(email, name, arrival, departure);
  }

  @Test
  public void createReservationInvalidOneMonthBefore() throws Exception {
    exceptionRule.expect(InvalidReservationDateException.class);
    exceptionRule.expectMessage("Too soon for this reservation. The campside can be reserved up to one month in advance.");

    String name = "John Doe";
    String email = "johndoe@email.com";
    LocalDate arrival = LocalDate.now().plusMonths(2);
    LocalDate departure = arrival.plusDays(1);

    reservationService.createReservation(email, name, arrival, departure);
  }

  @Test
  public void createReservationOverlaps() throws Exception {
    exceptionRule.expect(InvalidReservationDateException.class);
    exceptionRule.expectMessage("Already reserved. The campside is not available during this requested time.");

    String name = "John Doe";
    String email = "johndoe@email.com";
    LocalDate arrival = LocalDate.now().plusDays(4);
    LocalDate departure = LocalDate.now().plusDays(6);

    reservationService.createReservation(email, name, arrival, departure);

    name = "Jehn An";
    email = "jehnan@email.com";
    arrival = LocalDate.now().plusDays(5);
    departure = LocalDate.now().plusDays(7);

    reservationService.createReservation(email, name, arrival, departure);
  }

  @Test
  public void updateReservationDates() throws Exception {
    String name = "John Doe";
    String email = "johndoe@email.com";
    LocalDate arrival = LocalDate.now().plusDays(10);
    LocalDate departure = LocalDate.now().plusDays(11);

    Reservation reservation = reservationService.createReservation(email, name, arrival, departure);
    Long bookingId = reservation.getBookingId();
    assertNotNull(bookingId);
    assertEquals(reservation.getStatus(), ReservationStatus.CONFIRMED);

    arrival = arrival.minusDays(1);
    Reservation updateReservation = reservationService.updateReservationDates(bookingId, arrival, departure);

    assertEquals(arrival, updateReservation.getArrivalDate());
    assertEquals(updateReservation.getStatus(), ReservationStatus.CONFIRMED);
  }

  @Test
  public void cancelReservation() throws Exception {
    String name = "John Doe";
    String email = "johndoe@email.com";
    LocalDate arrival = LocalDate.now().plusDays(12);
    LocalDate departure = LocalDate.now().plusDays(14);

    Reservation reservation = reservationService.createReservation(email, name, arrival, departure);
    Long bookingId = reservation.getBookingId();
    assertNotNull(bookingId);
    assertEquals(reservation.getStatus(), ReservationStatus.CONFIRMED);

    Reservation cancelReservation = reservationService.cancelReservation(bookingId);
    assertEquals(cancelReservation.getStatus(), ReservationStatus.CANCELED);
  }

  @Test
  public void getAvailability() throws Exception {
    String name = "John Doe";
    String email = "johndoe@email.com";
    LocalDate arrival = LocalDate.now().plusDays(15);
    LocalDate departure = LocalDate.now().plusDays(17);
    reservationService.createReservation(email, name, arrival, departure);
    reservationService.createReservation(email, name, arrival.plusDays(4), departure.plusDays(4));

    List<Reservation> reservations = reservationService.getAvailability(LocalDate.now(), LocalDate.now().plusMonths(1));
    assertEquals(2, reservations.size());
  }
}