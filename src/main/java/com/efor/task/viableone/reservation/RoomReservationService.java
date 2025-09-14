package com.efor.task.viableone.reservation;

import com.efor.task.viableone.reservation.validation.IntervalValidatorException;
import com.efor.task.viableone.reservation.validation.RoomReservationValidatorException;

import java.time.Instant;
import java.util.List;
import java.util.Map;

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
     * @throws IntervalValidatorException if the interval is invalid
     */
    RoomReservationResult bookRoom(RoomReservation roomReservation);

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
