package com.efor.task.viableone.reservation.validation;

/**
 * Validates a room identifier.
 */
public interface RoomIdentifierValidator {
    /**
     * Validates the given room identifier.
     * @param roomIdentifier the identifier to validate
     * @throws RoomIdentifierValidatorException if the identifier is invalid
     */
    void validate(String roomIdentifier);
}
