package com.efor.task.viableone.reservation;

import java.time.Instant;

/**
 * Represents an existing reservation of a room.
 *
 * This immutable value object is returned by the service when reading reservations.
 *
 * @param roomId Unique identifier of the room that is reserved.
 * @param reservationStart Inclusive start of the reserved interval, as an {@link java.time.Instant} (UTC).
 * @param reservationEnd Exclusive end of the reserved interval, as an {@link java.time.Instant} (UTC).
 */
public record RoomReservationInfo(String roomId, Instant reservationStart, Instant reservationEnd) {
}
