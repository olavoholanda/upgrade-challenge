package com.upgrade.campside.domain.repository;

import com.upgrade.campside.domain.model.Reservation;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * The <code>ReservationRepository</code> interface extends Spring <code>JpaRepository</code
 * providing useful additional reservation related queries on the database. This is interface should be
 * used in the service layer for create, retrieve, update, delete and query operations on reservations.
 *
 * @author Olavo Holanda
 * @version 0.1
 * @since 0.1
 */
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

  @Query("select case when (count(r) > 0) then true else false end "
      + "from Reservation r where r.bookingId <> :bookingId"
      + " and r.status <> 'CANCELED'"
      + " and (r.arrivalDate between :startDate and :endDate"
      + " or r.departureDate between :startDate and :endDate)")
  boolean checkReservationOverlapsExceptOwn(@Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate, @Param("bookingId") Long bookingId);

  @Query("select case when (count(r) > 0) then true else false end "
      + "from Reservation r where r.status <> 'CANCELED'"
      + " and (r.arrivalDate between :startDate and :endDate"
      + " or r.departureDate between :startDate and :endDate)")
  boolean checkReservationOverlaps(@Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate);

  @Query("select r from Reservation r where "
      + " r.status <> 'CANCELED'"
      + " and (r.arrivalDate between :startDate and :endDate"
      + " or r.departureDate between :startDate and :endDate)")
  List<Reservation> getReservationsInPeriod(@Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate);
}
