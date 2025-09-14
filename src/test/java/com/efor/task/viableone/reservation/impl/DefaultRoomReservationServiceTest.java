package com.efor.task.viableone.reservation.impl;

import com.efor.task.viableone.reservation.ReservationConfig;
import com.efor.task.viableone.reservation.RoomReservation;
import com.efor.task.viableone.reservation.validation.IntervalValidatorException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import java.time.Instant;

@SpringBootTest(
        classes = {ReservationConfig.class}
)
class DefaultRoomReservationServiceTest {

    public DefaultRoomReservationServiceTest(@Autowired DefaultRoomReservationService roomReservationService) {
        this.service = roomReservationService;
    }

    private final DefaultRoomReservationService service;

    @BeforeEach
    void setUp() {
        service.reset();
    }

    @Test
    void bookRoom() {
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
        assertThatThrownBy(() -> service.getReservations("room-A"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getReservations() {
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
        assertThat(service.getAllReservations()).isEmpty();
    }

    @Test
    void getAllReservations() {
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

    @Test
    void findAvailableRoom_WrongInterval() {
        assertThatThrownBy(() ->
                service.findAvailableRoom(instant("2025-01-01T13:00:00Z"), instant("2025-01-01T12:00:00Z"))
        ).isInstanceOf(IntervalValidatorException.class);
    }

    @Test
    void findAvailableRoom_NoReservation() {
        assertThat(
                service.findAvailableRoom(instant("2025-01-01T12:00:00Z"), instant("2025-01-01T13:00:00Z"))
        ).isEmpty();
    }

    @Test
    void findAvailableRoom_Collision() {
        service.bookRoom(
                new RoomReservation(
                        "room-A",
                        instant("2025-01-01T11:00:00Z"),
                        instant("2025-01-01T14:00:00Z")
                )
        );

        assertThat(
                service.findAvailableRoom(instant("2025-01-01T12:00:00Z"), instant("2025-01-01T13:00:00Z"))
        ).isEmpty();
    }


    @Test
    void findAvailableRoom() {
        service.bookRoom(
                new RoomReservation(
                        "room-A",
                        instant("2025-01-01T13:00:00Z"),
                        instant("2025-01-01T14:00:00Z")
                )
        );

        assertThat(
                service.findAvailableRoom(instant("2025-01-01T12:00:00Z"), instant("2025-01-01T13:00:00Z"))
        ).isNotEmpty()
                .contains("room-A");
    }


    private Instant instant(String s) {
        return Instant.parse(s);
    }
}