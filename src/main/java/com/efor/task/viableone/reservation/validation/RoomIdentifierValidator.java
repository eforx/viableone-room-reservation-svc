package com.efor.task.viableone.reservation.validation;

/**
 * Validates a room identifier.
 */
public interface RoomIdentifierValidator {
    /**
     * Validates the given room identifier.
     * @param roomIdentifier
     * @throws RoomIdentifierValidatorException if the identifier is invalid
     */
    void validate(String roomIdentifier);
}
