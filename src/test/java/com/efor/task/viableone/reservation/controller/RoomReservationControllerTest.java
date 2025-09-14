package com.efor.task.viableone.reservation.controller;

import com.efor.task.viableone.app.RoomReservationApplicationConfig;
import com.efor.task.viableone.reservation.RoomReservation;
import com.efor.task.viableone.reservation.controller.dto.BookRoomRequest;
import com.efor.task.viableone.reservation.impl.DefaultRoomReservationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;

@SpringBootTest(
        classes = {
                RoomReservationApplicationConfig.class,
                JacksonAutoConfiguration.class,
                HttpMessageConvertersAutoConfiguration.class,
                WebMvcAutoConfiguration.class,
        }
)
@AutoConfigureMockMvc
class RoomReservationControllerTest {

    public RoomReservationControllerTest(@Autowired MockMvc mockMvc,
                                         @Autowired DefaultRoomReservationService roomReservationService) {
        this.mockMvc = mockMvc;
        this.roomReservationService = roomReservationService;
    }

    private final MockMvc mockMvc;
    private final DefaultRoomReservationService roomReservationService;
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private static final String BASE = "/api/v1/room";

    private static final String ROOM = "A-101";
    private static final Instant START = Instant.parse("2025-09-15T09:00:00Z");
    private static final Instant END = Instant.parse("2025-09-15T10:00:00Z");

    private String asJson(Object o) throws Exception {
        return objectMapper.writeValueAsString(o);
    }

    @BeforeEach
    void setUp() {
        roomReservationService.reset();
    }

    @Nested
    @DisplayName("POST /book")
    class BookRoom {
        @Test
        @DisplayName("returns 201 Created with payload when a new reservation is made")
        void bookRoom_created201() throws Exception {
            var request = new BookRoomRequest(ROOM, START, END);

            mockMvc.perform(
                            post(BASE + "/book")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(asJson(request))
                    ).andExpect(status().isCreated())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.roomId").value("A-101"))
                    .andExpect(jsonPath("$.reservationStart").value("2025-09-15T09:00:00Z"))
                    .andExpect(jsonPath("$.reservationEnd").value("2025-09-15T10:00:00Z"));
        }

        @Test
        @DisplayName("returns 409 Conflict with payload when interval conflicts (isNewReservation=false)")
        void bookRoom_conflict409() throws Exception {
            var request = new BookRoomRequest(ROOM, START, END);

            mockMvc.perform(
                    post(BASE + "/book")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJson(request))
            ).andExpect(status().isCreated());

            mockMvc.perform(
                            post(BASE + "/book")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(asJson(request))
                    )
                    .andExpect(status().isConflict());
        }

        @Test
        @DisplayName("returns 400 from feature handler - invalid interval")
        void bookRoom_intervalValidation400() throws Exception {
            var request = new BookRoomRequest(ROOM, END, START);

            mockMvc.perform(
                            post(BASE + "/book")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(asJson(request))
                    )
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("GET /available")
    class FindAvailableRoom {

        @Test
        @DisplayName("returns 200 OK with roomId when a room is available")
        void available_200() throws Exception {
            // TODO: mock candidate
            roomReservationService.bookRoom(
                    new RoomReservation(
                            ROOM,
                            Instant.parse("2025-09-15T08:00:00Z"),
                            Instant.parse("2025-09-15T09:00:00Z")
                    )
            );

            mockMvc.perform(
                            get(BASE + "/available")
                                    .param("start", START.toString())
                                    .param("end", END.toString())
                    )
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.roomId").value("A-101"));
        }

        @Test
        @DisplayName("returns 204 No Content when no room is available")
        void available_204() throws Exception {
            // TODO: mock candidate
            roomReservationService.bookRoom(new RoomReservation(ROOM, START, END));

            mockMvc.perform(
                            get(BASE + "/available")
                                    .param("start", START.toString())
                                    .param("end", END.toString())
                    )
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("returns 400 from feature handler")
        void available_intervalValidation400() throws Exception {
            mockMvc.perform(
                            get(BASE + "/available")
                                    .param("start", END.toString())
                                    .param("end", START.toString())
                    )
                    .andExpect(status().isBadRequest());
        }
    }
}