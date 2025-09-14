package com.efor.task.viableone.reservation.controller;


import com.efor.task.viableone.reservation.RoomReservation;
import com.efor.task.viableone.reservation.RoomReservationResult;
import com.efor.task.viableone.reservation.RoomReservationService;
import com.efor.task.viableone.reservation.controller.dto.BookRoomRequest;
import com.efor.task.viableone.reservation.controller.dto.BookRoomResponse;
import com.efor.task.viableone.reservation.controller.dto.FindAvailableRoomResponse;
import com.efor.task.viableone.reservation.validation.IntervalValidatorException;
import com.efor.task.viableone.reservation.validation.RoomReservationValidatorException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Optional;

/**
 * REST controller exposing endpoints for creating and querying room reservations.
 * <p>
 * The controller is intentionally thin and delegates all business logic to
 * {@link RoomReservationService}. It translates domain results into HTTP-friendly
 * responses and status codes.
 * </p>
 *
 * <p><strong>Time semantics:</strong> start is inclusive; end is exclusive. All instants are UTC.</p>
 */
@RestController
@RequestMapping(
        path = "/api/v1/room",
        produces = MediaType.APPLICATION_JSON_VALUE
)
@Validated
public class RoomReservationController {

    public RoomReservationController(RoomReservationService service) {
        this.service = service;
    }

    private final RoomReservationService service;

    /**
     * POST /reservations — attempt to book a room
     */
    @PostMapping("/book")
    public ResponseEntity<BookRoomResponse> bookRoom(@Valid @RequestBody BookRoomRequest request)
            throws RoomReservationValidatorException, IntervalValidatorException {

        RoomReservation domainReq = new RoomReservation(
                request.roomId(),
                request.reservationStart(),
                request.reservationEnd()
        );

        RoomReservationResult result = service.bookRoom(domainReq);

        BookRoomResponse body = new BookRoomResponse(
                result.roomId(),
                result.reservationStart(),
                result.reservationEnd()
        );

        // 201 when a new reservation was created; 409 when it conflicted (but we still return details)
        HttpStatus status = result.isNewReservation() ? HttpStatus.CREATED : HttpStatus.CONFLICT;
        return ResponseEntity.status(status).body(body);
    }

    /**
     * GET /rooms/available?start=...&end=... — find any available room for the interval
     * Returns 200 with a roomId if found, or 204 No Content if none available.
     */
    @GetMapping("/available")
    public ResponseEntity<FindAvailableRoomResponse> findAvailableRoom(
            @RequestParam("start") @NotNull Instant start,
            @RequestParam("end") @NotNull Instant end)
            throws IntervalValidatorException {

        Optional<String> room = service.findAvailableRoom(start, end);
        return room
                .map(id -> ResponseEntity.ok(new FindAvailableRoomResponse(id)))
                .orElseGet(() -> ResponseEntity.noContent().build());
    }
}