package com.efor.task.viableone.reservation.model;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import java.time.Instant;

class ReservationsTest {

    @Test
    void findCollision_NoCollisions() {
        var reservations = new Reservations();

        reservations.add(instant("2025-01-01T12:00:00Z"), instant("2025-01-01T13:00:00Z"));

        assertThat(reservations.findCollision(instant("2025-01-01T11:00:00Z"), instant("2025-01-01T12:00:00Z")))
                .isEmpty();
        assertThat(reservations.findCollision(instant("2025-01-01T13:00:00Z"), instant("2025-01-01T14:00:00Z")))
                .isEmpty();
        assertThat(reservations.findCollision(instant("2025-01-01T11:00:00Z"), instant("2025-01-01T11:59:59.999Z")))
                .isEmpty();
        assertThat(reservations.findCollision(instant("2025-01-01T13:00:00Z"), instant("2025-01-01T13:01:00Z")))
                .isEmpty();
    }

    @Test
    void findCollision_NoCollisions_LongerPeriod() {
        var reservations = new Reservations();

        reservations.add(instant("2024-12-13T12:00:00Z"), instant("2024-12-14T20:50:00Z"));
        reservations.add(instant("2024-12-15T08:45:00Z"), instant("2025-01-03T18:25:00Z"));

        assertThat(reservations.asList()).hasSize(2);
    }

    @Test
    void findCollision_NestedIntervalCollision() {
        var reservations = new Reservations();

        reservations.add(instant("2025-01-01T12:00:00Z"), instant("2025-01-01T13:00:00Z"));

        assertThat(reservations.findCollision(instant("2025-01-01T12:00:00Z"), instant("2025-01-01T13:00:00Z")))
                .isNotEmpty();
        assertThat(reservations.findCollision(instant("2025-01-01T12:01:00Z"), instant("2025-01-01T12:59:00Z")))
                .isNotEmpty();
        assertThat(reservations.findCollision(instant("2025-01-01T12:59:59.999Z"), instant("2025-01-01T13:00:00Z")))
                .isNotEmpty();
    }

    @Test
    void findCollision_OverlapIntervalCollision() {
        var reservations = new Reservations();

        reservations.add(instant("2025-01-01T12:00:00Z"), instant("2025-01-01T13:00:00Z"));

        assertThat(reservations.findCollision(instant("2025-01-01T11:59:00Z"), instant("2025-01-01T13:01:00Z")))
                .isNotEmpty();
        assertThat(reservations.findCollision(instant("2025-01-01T12:59:59.999Z"), instant("2025-01-01T13:00:00.001Z")))
                .isNotEmpty();
    }

    @Test
    void findCollisionPredecessorCollision() {
        var reservations = new Reservations();

        reservations.add(instant("2025-01-01T12:00:00Z"), instant("2025-01-01T13:00:00Z"));

        assertThat(reservations.findCollision(instant("2025-01-01T12:59:00Z"), instant("2025-01-01T13:01:00Z")))
                .isNotEmpty();
    }

    @Test
    void findCollisionPredecessorCollision_NotNormalized() {
        var reservations = new Reservations();

        reservations.add(instant("2025-01-01T12:00:00Z"), instant("2025-01-01T13:00:00Z"));

        assertThat(reservations.findCollision(instant("2025-01-01T12:59:59.999Z"), instant("2025-01-01T13:00:00Z")))
                .isNotEmpty();
    }

    @Test
    void findCollisionSuccessorCollision() {
        var reservations = new Reservations();

        reservations.add(instant("2025-01-01T12:00:00Z"), instant("2025-01-01T13:00:00Z"));

        assertThat(reservations.findCollision(instant("2025-01-01T11:59:00Z"), instant("2025-01-01T12:01:00Z")))
                .isNotEmpty();
    }

    @Test
    void addValid() {
        var reservations = new Reservations();

        reservations.add(instant("2025-01-01T12:00:00Z"), instant("2025-01-01T13:00:00Z"));
        assertThat(reservations.asList()).hasSize(1);

        reservations.add(instant("2025-01-01T13:00:00Z"), instant("2025-01-01T14:00:00Z"));
        assertThat(reservations.asList()).hasSize(2);

        reservations.add(instant("2025-01-01T12:59:59.999Z"), instant("2025-01-01T13:00:00Z"));
        assertThat(reservations.asList()).hasSize(3);
    }

    @Test
    void addInvalid_EndOfIntervalBeforeStart() {
        var reservations = new Reservations();

        assertThatThrownBy(() -> reservations.add(instant("2025-01-01T13:00:00Z"), instant("2025-01-01T12:00:00Z")))
                .isInstanceOf(IllegalArgumentException.class);

        assertThat(reservations.asList()).isEmpty();
    }

    @Test
    void addInvalid_ZeroIntervalLength() {
        var reservations = new Reservations();

        assertThatThrownBy(() -> reservations.add(instant("2025-01-01T13:00:00Z"), instant("2025-01-01T13:00:00Z")))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> reservations.add(instant("2025-01-01T13:00:00Z"), instant("2025-01-01T13:00:00.001Z")))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> reservations.add(instant("2025-01-01T12:59:00.001Z"), instant("2025-01-01T12:59:00.002Z")))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> reservations.add(instant("2025-01-01T12:59:58.999Z"), instant("2025-01-01T12:59:59.002Z")))
                .isInstanceOf(IllegalArgumentException.class);

        assertThat(reservations.asList()).isEmpty();
    }

    private Instant instant(String s) {
        return Instant.parse(s);
    }
}