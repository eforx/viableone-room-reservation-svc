package com.efor.task.viableone.reservation.impl;

import com.efor.task.viableone.reservation.RoomReservation;
import com.efor.task.viableone.reservation.validation.DefaultIntervalValidator;
import com.efor.task.viableone.reservation.validation.DefaultRoomReservationValidator;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Concurrency test: N threads concurrently call bookReservation on the SAME room.
 * Each thread generates 100 unique, non-overlapping intervals truncated to whole minutes.
 * Intervals across ALL threads are globally non-overlapping by construction.
 */
class DefaultRoomReservationServiceConcurrencyTest {

    @Test
    void bookRoom() throws Exception {
        var service = new DefaultRoomReservationService(new DefaultRoomReservationValidator(new DefaultIntervalValidator()));

        // --- Configuration ---
        final int threads = 3;                // configurable number of worker threads
        final int intervalsPerThread = 1000;  // each thread generates this many intervals
        final String roomId = "room-A";

        // Base start and slot sizing; everything is aligned to whole minutes.
        final Instant base = Instant.parse("2025-01-01T12:00:00Z").truncatedTo(ChronoUnit.MINUTES);
        final Duration slot = Duration.ofMinutes(1); // each reservation is 1 minute long

        // Thread pool and start barrier so workers begin at (roughly) the same time.
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CyclicBarrier startBarrier = new CyclicBarrier(threads);

        List<Future<?>> futures = new ArrayList<>(threads);

        for (int t = 0; t < threads; t++) {
            final int threadIndex = t;
            futures.add(pool.submit(() -> {
                try {
                    // Ensure all threads start generating at the same time.
                    startBarrier.await();

                    for (int j = 0; j < intervalsPerThread; j++) {
                        // Global slot index pattern: interleave by number of threads.
                        // This guarantees no overlap across threads:
                        // slotIndex = j * threads + threadIndex
                        long slotIndex = (long) j * threads + threadIndex;

                        Instant start = base.plus(slot.multipliedBy(slotIndex)).truncatedTo(ChronoUnit.MINUTES);
                        Instant end = start.plus(slot).truncatedTo(ChronoUnit.MINUTES);

                        // Sanity checks (defensive; should always hold).
                        if (!start.isBefore(end)) {
                            throw new IllegalStateException("Start must be before end");
                        }

                        // Invoke the service. The service should accept these non-overlapping intervals.
                        var result = service.bookRoom(new RoomReservation(roomId, start, end));
                        assertThat(result.isNewReservation()).isEqualTo(true);
                    }
                } catch (BrokenBarrierException | InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Worker interrupted", e);
                }
            }));
        }

        // Wait for completion and surface any exceptions.
        for (Future<?> f : futures) {
            f.get(30, TimeUnit.SECONDS);
        }

        pool.shutdown();
        if (!pool.awaitTermination(10, TimeUnit.SECONDS)) {
            pool.shutdownNow();
        }

        assertThat(service.getReservations(roomId))
                .hasSize(threads * intervalsPerThread);
    }
}