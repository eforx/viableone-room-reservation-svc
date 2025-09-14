package com.efor.task.viableone.reservation.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Response payload when querying for an available room.
 * If none is available, the API returns 204 No Content (no body).
 */
@Schema(
        name = "FindAvailableRoomResponse",
        description = "Contains the identifier of a room available for the requested interval."
)
public record FindAvailableRoomResponse(
        @Schema(description = "Identifier of an available room.", example = "R-101")
        String roomId
) {
}