package com.efor.task.viableone.reservation.controller;

import com.efor.task.viableone.reservation.validation.IntervalValidatorException;
import com.efor.task.viableone.reservation.validation.RoomIdentifierValidatorException;
import com.efor.task.viableone.reservation.validation.RoomReservationValidatorException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(assignableTypes = RoomReservationController.class)
public class RoomReservationExceptionHandler {
    private ResponseEntity<Object> body(HttpStatus status, String message) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("timestamp", Instant.now());
        m.put("status", status.value());
        m.put("error", status.getReasonPhrase());
        m.put("message", message);
        return ResponseEntity.status(status).body(m);
    }

    @ExceptionHandler(RoomReservationValidatorException.class)
    public ResponseEntity<Object> handleReservationValidation(RoomReservationValidatorException ex) {
        return body(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(IntervalValidatorException.class)
    public ResponseEntity<Object> handleInterval(IntervalValidatorException ex) {
        return body(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(RoomIdentifierValidatorException.class)
    public ResponseEntity<Object> handleInterval(RoomIdentifierValidatorException ex) {
        return body(HttpStatus.BAD_REQUEST, ex.getMessage());
    }
}
