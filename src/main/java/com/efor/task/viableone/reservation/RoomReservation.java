package com.efor.task.viableone.reservation;

import java.time.Instant;

/**
 * Represents a request to reserve a room for a specific time interval.
 *
 * This is an immutable value object used when attempting to create a reservation.
 *
 * @param roomId Unique identifier of the room to be reserved.
 * @param reservationStart Inclusive start of the reservation, as an {@link java.time.Instant} (UTC).
 * @param reservationEnd Exclusive end of the reservation, as an {@link java.time.Instant} (UTC).
 */
public record RoomReservation(String roomId, Instant reservationStart, Instant reservationEnd) {
}