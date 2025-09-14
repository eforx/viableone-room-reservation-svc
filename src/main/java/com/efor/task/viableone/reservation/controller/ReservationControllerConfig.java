package com.efor.task.viableone.reservation.controller;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Reservation feature Spring Context configuration.
 */
@Configuration
@Import({
        RoomReservationController.class,
        RoomReservationExceptionHandler.class
})
public class ReservationControllerConfig {
}
