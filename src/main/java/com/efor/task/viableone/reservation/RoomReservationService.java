package com.efor.task.viableone.reservation;

import com.efor.task.viableone.reservation.validation.IntervalValidatorException;
import com.efor.task.viableone.reservation.validation.RoomReservationValidatorException;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service API for creating and querying room reservations.
 * Implementations are responsible for ensuring that booking operations are consistent.
 */
public interface RoomReservationService {

    /**
     * Attempts to reserve a room for the specified time interval.
     * <p>
     * If the requested interval conflicts with an existing one, the returned
     * {@link RoomReservationResult} will have {@code isNewReservation = false} and will include
     * the conflicting interval; on success it will have {@code isNewReservation = true}.
     * </p>
     *
     * @param roomReservation the reservation request
     * @return the outcome of the booking attempt; never {@code null}
     * @throws IllegalStateException             if the booking cannot be processed at the moment
     * @throws RoomReservationValidatorException if the room reservation request is invalid
     * @throws IntervalValidatorException        if the interval is invalid
     */
    RoomReservationResult bookRoom(RoomReservation roomReservation);

    /**
     * Finds any room that is available for the entire requested interval without creating a reservation.
     * <p>
     * The start instant is inclusive and the end instant is exclusive. If multiple rooms satisfy the
     * request, the particular room chosen is implementation-defined and not guaranteed to be stable.
     * </p>
     *
     * @param reservationStart inclusive start of the requested interval; must not be {@code null}
     * @param reservationEnd   exclusive end of the requested interval; must not be {@code null} and must be after {@code reservationStart}
     * @return an {@code Optional} containing the identifier of an available room, or {@code Optional.empty()} if none is available
     * @throws IntervalValidatorException if the interval violates configured constraints
     */
    Optional<String> findAvailableRoom(Instant reservationStart, Instant reservationEnd);

    /**
     * Returns all reservations for the given room.
     *
     * @param roomId the room identifier
     * @return a list of reservations; never {@code null}
     * @throws IllegalArgumentException if the room is unknown
     */
    List<RoomReservationInfo> getReservations(String roomId);

    /**
     * Returns all reservations grouped by room id.
     *
     * @return a map of reservations keyed by room id; never {@code null}
     */
    Map<String, List<RoomReservationInfo>> getAllReservations();
}
