package com.efor.task.viableone.reservation.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

/**
 * Response payload returned after attempting to book a room.
 * Uses HTTP 201 for success, 409 when a conflicting reservation exists.
 */
@Schema(
        name = "BookRoomResponse",
        description = "Echoes the room id and interval that were processed by the booking endpoint."
)
public record BookRoomResponse(
        @Schema(description = "Identifier of the room related to the reservation.", example = "R-101")
        String roomId,

        @Schema(description = "Inclusive start of the reservation window (UTC).", format = "date-time", example = "2025-09-20T08:00:00Z")
        Instant reservationStart,

        @Schema(description = "Exclusive end of the reservation window (UTC).", format = "date-time", example = "2025-09-20T10:00:00Z")
        Instant reservationEnd
) {
}