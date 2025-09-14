package com.efor.task.viableone.reservation.controller;

import com.efor.task.viableone.app.RoomReservationApplicationConfig;
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
class RoomReservationControllerAcceptanceTest {

    public RoomReservationControllerAcceptanceTest(@Autowired MockMvc mockMvc,
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

    private String asJson(Object o) throws Exception {
        return objectMapper.writeValueAsString(o);
    }

    @BeforeEach
    void setUp() {
        roomReservationService.reset();
    }

    @Nested
    @DisplayName("Happy Path")
    class BookRoom {
        @Test
        @DisplayName("scenario 1 - book")
        void bookRoomDuplication_and_findAvailableRoom() throws Exception {
            var bookRequest1 = new BookRoomRequest(
                    "A-101",
                    Instant.parse("2025-07-01T08:00:00Z"),
                    Instant.parse("2025-07-01T10:00:00Z")
            );

            mockMvc.perform(
                    post(BASE + "/book")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJson(bookRequest1))
            ).andExpect(status().isCreated());

            var bookRequest2 = new BookRoomRequest(
                    "A-101",
                    Instant.parse("2025-07-01T09:00:00Z"),
                    Instant.parse("2025-07-01T11:00:00Z")
            );

            mockMvc.perform(
                    post(BASE + "/book")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJson(bookRequest2))
            ).andExpect(status().isConflict());
        }

        @Test
        @DisplayName("scenario 2 - available")
        void findAvailableRoom_afterBooking() throws Exception {
            var bookRequest1 = new BookRoomRequest(
                    "A-101",
                    Instant.parse("2025-07-01T08:00:00Z"),
                    Instant.parse("2025-07-01T10:00:00Z")
            );

            mockMvc.perform(
                    post(BASE + "/book")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJson(bookRequest1))
            ).andExpect(status().isCreated());

            var bookRequest2 = new BookRoomRequest(
                    "A-101",
                    Instant.parse("2025-07-01T09:00:00Z"),
                    Instant.parse("2025-07-01T11:00:00Z")
            );

            mockMvc.perform(
                    post(BASE + "/book")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJson(bookRequest2))
            ).andExpect(status().isConflict());

            mockMvc.perform(
                            get(BASE + "/available")
                                    .param("start", "2025-07-01T08:00:00Z")
                                    .param("end", "2025-07-01T09:00:00Z")
                    )
                    .andExpect(status().isNoContent());
        }
    }
}