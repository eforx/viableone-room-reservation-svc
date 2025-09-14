package com.efor.task.viableone.reservation.validation;

import com.efor.task.viableone.reservation.RoomReservation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DefaultRoomReservationValidator implements RoomReservationValidator {

    public DefaultRoomReservationValidator(IntervalValidator intervalValidator) {
        this.intervalValidator = intervalValidator;
    }

    private static final Logger logger = LoggerFactory.getLogger(DefaultRoomReservationValidator.class);

    private final IntervalValidator intervalValidator;

    @Override
    public void validate(RoomReservation reservation) {
        logger.debug("Validating room reservation. reservation={}", reservation);

        if (reservation == null) {
            throw new RoomReservationValidatorException("Room reservation must not be null");
        }

        if (reservation.roomId() == null || reservation.roomId().isBlank()) {
            throw new RoomReservationValidatorException("Room reservation must have set a roomId");
        }

        intervalValidator.validate(reservation.reservationStart(), reservation.reservationEnd());
    }
}
