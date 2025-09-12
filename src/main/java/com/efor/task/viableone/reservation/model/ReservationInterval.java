package com.efor.task.viableone.reservation.model;
import java.time.Instant;
import java.util.Objects;

/**
 * Immutable half-open interval {@code [start, end)} in UTC.
 * <p>
 * <strong>Invariants:</strong>
 * <ul>
 *   <li>{@code start != null}, {@code end != null}</li>
 *   <li>{@code start < end}</li>
 * </ul>
 * </p>
 */
public record ReservationInterval(Instant start, Instant end) {
    /**
     * Creates a new interval.
     *
     * @throws NullPointerException if {@code start} or {@code end} is {@code null}
     * @throws IllegalArgumentException if {@code start} is not strictly before {@code end}
     */
    public ReservationInterval {
        Objects.requireNonNull(start, "ReservationInterval.start must not be null");
        Objects.requireNonNull(end, "ReservationInterval.end must not be null");
        if (!start.isBefore(end)) {
            throw new IllegalArgumentException("start must be < end");
        }
    }

    /**
     * Returns true if this interval overlaps the other using half-open semantics.
     */
    public boolean overlaps(ReservationInterval other) {
        return this.start.isBefore(other.end) && other.start.isBefore(this.end);
    }
}
