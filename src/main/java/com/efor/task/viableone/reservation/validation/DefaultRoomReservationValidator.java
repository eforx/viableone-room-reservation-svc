package com.efor.task.viableone.reservation.validation;

import com.efor.task.viableone.reservation.RoomReservation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DefaultRoomReservationValidator implements RoomReservationValidator {

    public DefaultRoomReservationValidator(
            RoomIdentifierValidator roomIdentifierValidator,
            IntervalValidator intervalValidator) {
        this.roomIdentifierValidator = roomIdentifierValidator;
        this.intervalValidator = intervalValidator;
    }

    private static final Logger logger = LoggerFactory.getLogger(DefaultRoomReservationValidator.class);

    private final IntervalValidator intervalValidator;
    private final RoomIdentifierValidator roomIdentifierValidator;

    @Override
    public void validate(RoomReservation reservation) {
        logger.debug("Validating room reservation. reservation={}", reservation);

        if (reservation == null) {
            throw new RoomReservationValidatorException("Room reservation must not be null");
        }

        try {
            roomIdentifierValidator.validate(reservation.roomId());
            intervalValidator.validate(reservation.reservationStart(), reservation.reservationEnd());
        } catch (Exception e) {
            throw new RoomReservationValidatorException("Room reservation validation failed", e);
        }
    }
}
