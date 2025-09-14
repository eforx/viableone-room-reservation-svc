package com.efor.task.viableone.reservation.validation;

import java.time.Instant;

/**
 * Validates a interval {@code [start, end]}
 */
public interface IntervalValidator {
    /**
     * Validates the given interval.
     *
     * @param intervalStart Inclusive start of the reservation, as an {@link java.time.Instant} (UTC).
     * @param intervalEnd   Exclusive end of the reservation, as an {@link java.time.Instant} (UTC).
     */
    void validate(Instant intervalStart, Instant intervalEnd);
}
