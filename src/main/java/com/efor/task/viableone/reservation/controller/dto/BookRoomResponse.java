package com.efor.task.viableone.reservation.controller.dto;

import java.time.Instant;

/**
 * Response payload returned after attempting to book a room.
 * <p>
 * Contains the room identifier and the reservation interval that the server
 * processed. The semantics of the HTTP status code indicate whether a new
 * reservation was created (201) or a conflict occurred (409).
 * </p>
 *
 * @param roomId            identifier of the room related to the reservation
 * @param reservationStart  inclusive start of the reservation window (UTC)
 * @param reservationEnd    exclusive end of the reservation window (UTC)
 */
public record BookRoomResponse(
        String roomId,
        Instant reservationStart,
        Instant reservationEnd
) {}
