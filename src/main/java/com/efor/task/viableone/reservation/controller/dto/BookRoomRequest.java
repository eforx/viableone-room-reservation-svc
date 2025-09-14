package com.efor.task.viableone.reservation.controller.dto;

import java.time.Instant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Request payload used to book a room for a specific time interval.
 * <p>
 * The {@code reservationStart} is inclusive and the {@code reservationEnd} is exclusive.
 * All instants are expected to be in UTC.
 * </p>
 *
 * @param roomId            unique identifier of the room to reserve; must not be blank
 * @param reservationStart  inclusive start of the reservation window; must not be {@code null}
 * @param reservationEnd    exclusive end of the reservation window; must not be {@code null}
 */
public record BookRoomRequest(
        @NotBlank String roomId,
        @NotNull Instant reservationStart,
        @NotNull Instant reservationEnd
) {

}