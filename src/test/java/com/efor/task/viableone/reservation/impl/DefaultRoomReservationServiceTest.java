package com.efor.task.viableone.reservation.impl;

import com.efor.task.viableone.reservation.RoomReservation;
import com.efor.task.viableone.reservation.validation.DefaultIntervalValidator;
import com.efor.task.viableone.reservation.validation.DefaultRoomReservationValidator;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import java.time.Instant;

class DefaultRoomReservationServiceTest {

    @Test
    void bookRoom() {
        DefaultRoomReservationService service = createService();

        var result = service.bookRoom(
                new RoomReservation(
                        "room-A",
                        instant("2025-01-01T12:00:00Z"),
                        instant("2025-01-01T13:00:00Z")
                )
        );

        assertThat(result.isNewReservation()).isEqualTo(true);
        assertThat(result.roomId()).isEqualTo("room-A");
        assertThat(result.reservationStart()).isEqualTo(instant("2025-01-01T12:00:00Z"));
        assertThat(result.reservationEnd()).isEqualTo(instant("2025-01-01T13:00:00Z"));
    }

    @Test
    void bookRoom_TrimRoomName() {
        DefaultRoomReservationService service = createService();

        var result = service.bookRoom(
                new RoomReservation(
                        "  room-A ",
                        instant("2025-01-01T12:00:00Z"),
                        instant("2025-01-01T13:00:00Z")
                )
        );

        assertThat(result.isNewReservation()).isEqualTo(true);
        assertThat(result.roomId()).isEqualTo("room-A");
        assertThat(result.reservationStart()).isEqualTo(instant("2025-01-01T12:00:00Z"));
        assertThat(result.reservationEnd()).isEqualTo(instant("2025-01-01T13:00:00Z"));
    }

    @Test
    void getReservations_empty() {
        DefaultRoomReservationService service = createService();

        assertThatThrownBy(() -> service.getReservations("room-A"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getReservations() {
        DefaultRoomReservationService service = createService();

        service.bookRoom(
                new RoomReservation(
                        "room-A",
                        instant("2025-01-01T12:00:00Z"),
                        instant("2025-01-01T13:00:00Z")
                )
        );

        service.bookRoom(
                new RoomReservation(
                        "room-A",
                        instant("2025-01-01T14:00:00Z"),
                        instant("2025-01-01T15:00:00Z")
                )
        );

        service.bookRoom(
                new RoomReservation(
                        "room-B",
                        instant("2025-01-01T12:00:00Z"),
                        instant("2025-01-01T13:00:00Z")
                )
        );

        assertThat(service.getReservations("room-A"))
                .hasSize(2)
                .allMatch(roomReservationInfo -> roomReservationInfo.roomId().equals("room-A"));
        assertThat(service.getReservations("room-B"))
                .hasSize(1)
                .allMatch(roomReservationInfo -> roomReservationInfo.roomId().equals("room-B"));
    }

    @Test
    void getAllReservations_empty() {
        DefaultRoomReservationService service = createService();

        assertThat(service.getAllReservations()).isEmpty();
    }

    @Test
    void getAllReservations() {
        DefaultRoomReservationService service = createService();

        service.bookRoom(
                new RoomReservation(
                        "room-A",
                        instant("2025-01-01T12:00:00Z"),
                        instant("2025-01-01T13:00:00Z")
                )
        );

        service.bookRoom(
                new RoomReservation(
                        "room-A",
                        instant("2025-01-01T14:00:00Z"),
                        instant("2025-01-01T15:00:00Z")
                )
        );

        service.bookRoom(
                new RoomReservation(
                        "room-B",
                        instant("2025-01-01T12:00:00Z"),
                        instant("2025-01-01T13:00:00Z")
                )
        );

        var result = service.getAllReservations();
        assertThat(result).hasSize(2);
        assertThat(result.get("room-A")).hasSize(2);
        assertThat(result.get("room-B")).hasSize(1);
    }

    private Instant instant(String s) {
        return Instant.parse(s);
    }

    private DefaultRoomReservationService createService() {
        return new DefaultRoomReservationService(new DefaultRoomReservationValidator(new DefaultIntervalValidator()));
    }

}