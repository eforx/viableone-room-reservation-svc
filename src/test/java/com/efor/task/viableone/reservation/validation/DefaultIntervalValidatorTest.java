package com.efor.task.viableone.reservation.validation;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import java.time.Instant;

class DefaultIntervalValidatorTest {

    @Test
    void validate_IntervalStartNull() {
        DefaultIntervalValidator validator = new DefaultIntervalValidator();

        assertThatThrownBy(() -> validator.validate(
                null,
                instant("2025-01-01T13:00:00Z")
        )).isInstanceOf(IntervalValidatorException.class);
    }

    @Test
    void validate_IntervalEndNull() {
        DefaultIntervalValidator validator = new DefaultIntervalValidator();

        assertThatThrownBy(() -> validator.validate(
                instant("2025-01-01T13:00:00Z"),
                null
        )).isInstanceOf(IntervalValidatorException.class);
    }

    @Test
    void validate_IntervalStartBeforeEnd() {
        DefaultIntervalValidator validator = new DefaultIntervalValidator();

        assertThatThrownBy(() -> validator.validate(
                instant("2025-01-01T13:00:00Z"),
                instant("2025-01-01T12:00:00Z")
        )).isInstanceOf(IntervalValidatorException.class);
    }

    @Test
    void validate_IntervalStartEqualsEnd() {
        DefaultIntervalValidator validator = new DefaultIntervalValidator();

        assertThatThrownBy(() -> validator.validate(
                instant("2025-01-01T12:00:00Z"),
                instant("2025-01-01T12:00:00Z")
        )).isInstanceOf(IntervalValidatorException.class);
    }

    private Instant instant(String s) {
        return Instant.parse(s);
    }
}