package com.efor.task.viableone.reservation.controller;

import com.efor.task.viableone.reservation.RoomReservation;
import com.efor.task.viableone.reservation.RoomReservationResult;
import com.efor.task.viableone.reservation.RoomReservationService;
import com.efor.task.viableone.reservation.controller.dto.BookRoomRequest;
import com.efor.task.viableone.reservation.controller.dto.BookRoomResponse;
import com.efor.task.viableone.reservation.controller.dto.FindAvailableRoomResponse;
import com.efor.task.viableone.reservation.validation.IntervalValidatorException;
import com.efor.task.viableone.reservation.validation.RoomReservationValidatorException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(
        name = "Room Reservations",
        description = """
                Endpoints to book a room and to query for an available room over a time interval.
                Time semantics: start inclusive, end exclusive. Instants are treated as UTC.
                """
)
public class RoomReservationController {

    public RoomReservationController(RoomReservationService service) {
        this.service = service;
    }

    private final RoomReservationService service;

    @Operation(
            summary = "Book a room",
            description = """
                    Attempts to create a reservation for the given room and interval.
                    Returns 201 Created when a new reservation is made; 409 Conflict when the interval conflicts with an existing reservation (the response still includes the interval that was processed).
                    """,
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Booking payload with room id and time interval (UTC).",
                    content = @Content(
                            schema = @Schema(implementation = BookRoomRequest.class),
                            examples = @ExampleObject(name = "Sample booking", value = """
                                    {
                                      "roomId": "R-101",
                                      "reservationStart": "2025-09-20T08:00:00Z",
                                      "reservationEnd": "2025-09-20T10:00:00Z"
                                    }
                                    """)
                    )
            )
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Reservation created.",
                    content = @Content(schema = @Schema(implementation = BookRoomResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflicting reservation exists.",
                    content = @Content(schema = @Schema(implementation = BookRoomResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Malformed request.", content = @Content),
            @ApiResponse(responseCode = "422", description = "Validation failed.", content = @Content),
            @ApiResponse(responseCode = "500", description = "Server error.", content = @Content)
    })
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

    @Operation(
            summary = "Find an available room",
            description = """
                    Returns any room that is fully available for the given interval (no reservation is created).
                    Responds with 200 and a room id if found; 204 No Content if no room is available.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Available room found.",
                    content = @Content(schema = @Schema(implementation = FindAvailableRoomResponse.class))
            ),
            @ApiResponse(responseCode = "204", description = "No room available for the entire interval.", content = @Content),
            @ApiResponse(responseCode = "400", description = "Malformed request.", content = @Content),
            @ApiResponse(responseCode = "422", description = "Invalid or illogical interval.", content = @Content),
            @ApiResponse(responseCode = "500", description = "Server error.", content = @Content)
    })
    @GetMapping("/available")
    public ResponseEntity<FindAvailableRoomResponse> findAvailableRoom(
            @Parameter(
                    description = "Inclusive start of the interval (UTC, RFC3339).",
                    required = true,
                    example = "2025-09-20T08:00:00Z",
                    schema = @Schema(type = "string", format = "date-time")
            )
            @RequestParam("start") @NotNull Instant start,
            @Parameter(
                    description = "Exclusive end of the interval (UTC, RFC3339).",
                    required = true,
                    example = "2025-09-20T10:00:00Z",
                    schema = @Schema(type = "string", format = "date-time")
            )
            @RequestParam("end") @NotNull Instant end)
            throws IntervalValidatorException {

        Optional<String> room = service.findAvailableRoom(start, end);
        return room
                .map(id -> ResponseEntity.ok(new FindAvailableRoomResponse(id)))
                .orElseGet(() -> ResponseEntity.noContent().build());
    }
}