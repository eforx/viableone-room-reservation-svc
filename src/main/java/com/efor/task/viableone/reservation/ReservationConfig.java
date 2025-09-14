package com.efor.task.viableone.reservation;

import com.efor.task.viableone.reservation.impl.DefaultRoomReservationService;
import com.efor.task.viableone.reservation.validation.DefaultIntervalValidator;
import com.efor.task.viableone.reservation.validation.DefaultRoomReservationValidator;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Reservation feature Spring Context configuration.
 */
@Configuration
@Import({
        DefaultRoomReservationService.class,
        DefaultIntervalValidator.class,
        DefaultRoomReservationValidator.class
})
public class ReservationConfig {
}
