package com.efor.task.viableone.reservation.validation;

import com.efor.task.viableone.reservation.RoomReservation;

/**
 * Validates a {@link RoomReservation}.
 */
public interface RoomReservationValidator {
    /**
     * Validates the given {@link RoomReservation}.
     * @param reservation the reservation to validate
     * @throws RoomReservationValidatorException if the reservation is invalid
     */
    void validate(RoomReservation reservation);
}
