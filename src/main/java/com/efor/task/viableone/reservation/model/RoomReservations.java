package com.efor.task.viableone.reservation.model;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.NavigableMap;
import java.util.Optional;
import java.util.TreeMap;

/**
 * Manages non-overlapping {@link ReservationInterval} reservations for a single resource.
 *
 * <p><strong>Normalization:</strong> All instants are truncated to minutes via {@link #normalize(Instant)}.</p>
 *
 * <p><strong>Complexity summary</strong> (n = number of reservations):</p>
 * <ul>
 *   <li>{@link #add(Instant, Instant) Add}: O(log n)</li>
 *   <li>{@link #findCollision(Instant, Instant) findCollision}: O(log n)</li>
 *   <li>{@link #asList() asList}: O(n)</li>
 *   <li>{@link #size() size}: O(1)</li>
 * </ul>
 *
 * <p><strong>Thread-safety:</strong> Not thread-safe.</p>
 */
public class RoomReservations {
    /**
     * Ordered by interval start time.
     */
    private final NavigableMap<Instant, ReservationInterval> intervals = new TreeMap<>();

    /**
     * Finds a colliding interval with {@code [start, end)} if one exists.
     *
     * <p>The check only needs to consider the predecessor and successor intervals
     * relative to the candidate's start time in the start-ordered map.</p>
     *
     * <p><strong>Time complexity:</strong> O(log n) to locate neighbors in the tree.</p>
     *
     * @param start inclusive start instant (will be truncated to minutes)
     * @param end exclusive end instant (must be after {@code start})
     * @return an {@code Optional} containing the conflicting interval, or empty if none
     * @throws NullPointerException if {@code start} or {@code end} is null
     * @throws IllegalArgumentException if {@code start} is not before {@code end}
     */
    public Optional<ReservationInterval> findCollision(Instant start, Instant end) {
        var normalizedStart = normalize(start);
        var normalizedEnd = normalize(end);
        var candidate = new ReservationInterval(normalizedStart, normalizedEnd);

        var predecessor = intervals.floorEntry(normalizedStart);
        if (predecessor != null && predecessor.getValue().overlaps(candidate)) {
            return Optional.of(predecessor.getValue());
        }
        var successor = intervals.ceilingEntry(normalizedStart);
        if (successor != null && successor.getValue().overlaps(candidate)) {
            return Optional.of(successor.getValue());
        }
        return Optional.empty();
    }

    /**
     * Returns true if there is no interval with {@code [start, end]}.
     *
     * Use {@link #findCollision(Instant, Instant)} to find a conflicting interval.
     * @param start inclusive start instant (will be truncated to minutes)
     * @param end exclusive end instant (must be after {@code start})
     * @return true if there is no interval with {@code [start, end]}
     */
    public boolean hasNoConflict(Instant start, Instant end) {
        return findCollision(start, end).isEmpty();
    }

    /**
     * Adds the interval {@code [start, end)} without performing a conflict check.
     *
     * <p>Call {@link #findCollision(Instant, Instant)} first if you need to prevent overlaps.</p>
     *
     * <p><strong>Time complexity:</strong> O(log n) for the tree insertion.</p>
     *
     * @param start inclusive start instant (will be truncated to minutes)
     * @param end exclusive end instant (must be after {@code start})
     * @return the added interval
     * @throws NullPointerException if {@code start} or {@code end} is null
     * @throws IllegalArgumentException if {@code start} is not before {@code end}
     */
    public ReservationInterval add(Instant start, Instant end) {
        var normalizedStart = normalize(start);
        var normalizedEnd = normalize(end);
        var interval = new ReservationInterval(normalizedStart, normalizedEnd);
        intervals.put(normalizedStart, interval);
        return interval;
    }

    /**
     * Returns an immutable snapshot of all reservations in ascending start-time order.
     *
     * @return immutable, start-ordered snapshot
     */
    public List<ReservationInterval> asList() {
        return List.copyOf(intervals.values());
    }

    /**
     * Returns the number of intervals.
     *
     * @return current number of reservations
     */
    public int size() {
        return intervals.size();
    }

    private Instant normalize(Instant instant) {
        return instant.truncatedTo(ChronoUnit.MINUTES);
    }
}
