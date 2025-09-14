package com.efor.task.viableone.reservation.validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DefaultRoomIdentifierValidator implements RoomIdentifierValidator {
    private static final Logger logger = LoggerFactory.getLogger(DefaultRoomIdentifierValidator.class);

    @Override
    public void validate(String roomIdentifier) {
        logger.debug("Validating room identifier. roomIdentifier='{}'", roomIdentifier);

        if (roomIdentifier == null) {
            throw new RoomIdentifierValidatorException("Room reservation must have set a roomId");
        }

        if (roomIdentifier.isBlank()) {
            throw new RoomIdentifierValidatorException("Room reservation must not be blank");
        }
    }
}
