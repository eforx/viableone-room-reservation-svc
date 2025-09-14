package com.efor.task.viableone.reservation.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

/**
 * Request payload used to book a room for a specific time interval.
 * The {@code reservationStart} is inclusive and {@code reservationEnd} is exclusive. UTC expected.
 */
@Schema(
        name = "BookRoomRequest",
        description = "Booking payload with the room identifier and the desired interval (UTC)."
)
public record BookRoomRequest(
        @Schema(
                description = "Unique identifier of the room to reserve.",
                example = "R-101",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank String roomId,

        @Schema(
                description = "Inclusive start of the reservation window (UTC, RFC3339).",
                example = "2025-09-20T08:00:00Z",
                format = "date-time",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull Instant reservationStart,

        @Schema(
                description = "Exclusive end of the reservation window (UTC, RFC3339).",
                example = "2025-09-20T10:00:00Z",
                format = "date-time",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull Instant reservationEnd
) {

}