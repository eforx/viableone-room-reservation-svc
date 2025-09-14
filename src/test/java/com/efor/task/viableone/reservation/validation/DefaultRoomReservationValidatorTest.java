package com.efor.task.viableone.reservation.validation;

import com.efor.task.viableone.reservation.RoomReservation;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import java.time.Instant;

class DefaultRoomReservationValidatorTest {

    @Test
    void validate_RoomNameNull() {
        DefaultRoomReservationValidator validator = createValidator();

        assertThatThrownBy(() -> validator.validate(
                new RoomReservation(
                        null,
                        instant("2025-01-01T12:00:00Z"),
                        instant("2025-01-01T13:00:00Z")
                )
        )).isInstanceOf(RoomReservationValidatorException.class);
    }

    @Test
    void validate_RoomNameBlank() {
        DefaultRoomReservationValidator validator = createValidator();

        assertThatThrownBy(() -> validator.validate(
                new RoomReservation(
                        "     ",
                        instant("2025-01-01T12:00:00Z"),
                        instant("2025-01-01T13:00:00Z")
                )
        )).isInstanceOf(RoomReservationValidatorException.class);
    }

    @Test
    void validate_IntervalStartBeforeEnd() {
        DefaultRoomReservationValidator validator = createValidator();

        assertThatThrownBy(() -> validator.validate(
                new RoomReservation(
                        "room-A",
                        instant("2025-01-01T13:00:00Z"),
                        instant("2025-01-01T12:00:00Z")
                )
        )).isInstanceOf(RoomReservationValidatorException.class);
    }

    private Instant instant(String s) {
        return Instant.parse(s);
    }

    private DefaultRoomReservationValidator createValidator() {
        return new DefaultRoomReservationValidator(
                new DefaultRoomIdentifierValidator(),
                new DefaultIntervalValidator()
        );
    }
}