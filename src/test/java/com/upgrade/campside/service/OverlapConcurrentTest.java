package com.upgrade.campside.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.upgrade.campside.domain.model.Reservation;
import com.upgrade.campside.domain.model.ReservationStatus;
import com.upgrade.campside.domain.repository.ReservationRepository;
import com.upgrade.campside.exception.InvalidReservationDateException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
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
public class OverlapConcurrentTest {

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
  public void createConcurrentReservation() throws Exception {
    ExecutorService service = Executors.newFixedThreadPool(5);
    AtomicInteger success = new AtomicInteger(0);
    AtomicInteger fails = new AtomicInteger(0);

    IntStream.range(0, 5)
        .forEach(index -> service.submit(() -> {
          if (createReservation()) {
            success.getAndIncrement();
          } else {
            fails.getAndIncrement();
          }
        }));

    service.awaitTermination(5, TimeUnit.SECONDS);

    assertEquals(success.get(), 1);
    assertEquals(fails.get(), 4);
  }

  private boolean createReservation() {
    String name = "John Doe";
    String email = "johndoe@email.com";
    LocalDate arrival = LocalDate.now().plusDays(2);
    LocalDate departure = LocalDate.now().plusDays(3);

    try {
      reservationService
          .createReservation(email, name, arrival, departure);
      return true;
    } catch (InvalidReservationDateException e) {
      return false;
    }
  }
}