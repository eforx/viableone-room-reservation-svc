package com.efor.task.viableone.reservation.validation;

public class RoomReservationValidatorException extends RuntimeException {
    public RoomReservationValidatorException(String message) {
        super(message);
    }

    public RoomReservationValidatorException(String message, Throwable cause) {
        super(message, cause);
    }
}
