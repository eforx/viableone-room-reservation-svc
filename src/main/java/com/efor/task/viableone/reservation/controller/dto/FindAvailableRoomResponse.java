package com.efor.task.viableone.reservation.controller.dto;

/**
 * Response payload returned when querying for an available room.
 * <p>
 * If a room is available for the entire requested interval, {@code roomId}
 * contains its identifier. If no room is available, the controller returns
 * HTTP 204 No Content instead of this body.
 * </p>
 *
 * @param roomId identifier of an available room
 */
public record FindAvailableRoomResponse(String roomId) {}