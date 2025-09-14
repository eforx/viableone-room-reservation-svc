package com.efor.task.viableone.reservation.validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class DefaultIntervalValidator implements IntervalValidator {

    private static final Logger logger = LoggerFactory.getLogger(DefaultIntervalValidator.class);

    @Override
    public void validate(Instant intervalStart, Instant intervalEnd) {
        logger.debug("Validating interval. intervalStart={}, intervalEnd={}", intervalStart, intervalEnd);

        if (intervalStart == null) {
            throw new IntervalValidatorException("Room reservation must have set a reservationStart");
        }

        if (intervalEnd == null) {
            throw new IntervalValidatorException("Room reservation must have set a reservationEnd");
        }

        var intervalCompareResult = intervalStart.compareTo(intervalEnd);
        if (intervalCompareResult == 0) {
            throw new IntervalValidatorException("Room reservation start and end must not be equal");
        }

        if (intervalCompareResult > 0) {
            throw new IntervalValidatorException("Room reservation start must be before end");
        }
    }
}
