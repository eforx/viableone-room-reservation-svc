package com.efor.task.viableone.reservation.impl;

import com.efor.task.viableone.reservation.RoomReservation;
import com.efor.task.viableone.reservation.RoomReservationInfo;
import com.efor.task.viableone.reservation.RoomReservationResult;
import com.efor.task.viableone.reservation.RoomReservationService;
import com.efor.task.viableone.reservation.model.ReservationInterval;
import com.efor.task.viableone.reservation.model.RoomReservations;
import com.efor.task.viableone.reservation.validation.IntervalValidator;
import com.efor.task.viableone.reservation.validation.RoomIdentifierValidator;
import com.efor.task.viableone.reservation.validation.RoomReservationValidator;
import com.google.common.util.concurrent.Striped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

/**
 * Default in-memory implementation of {@link RoomReservationService}.
 * <p>
 * This service keeps reservations per room in memory and ensures that booking operations
 * for the same room are processed in a mutually exclusive manner.
 * </p>
 */
@Service
public class DefaultRoomReservationService implements RoomReservationService {

    public DefaultRoomReservationService(RoomReservationValidator roomReservationValidator,
                                         RoomIdentifierValidator roomIdentifierValidator,
                                         IntervalValidator intervalValidator) {
        this.roomReservationValidator = roomReservationValidator;
        this.roomIdentifierValidator = roomIdentifierValidator;
        this.intervalValidator = intervalValidator;
    }

    private static final Logger logger = LoggerFactory.getLogger(DefaultRoomReservationService.class);

    private final RoomReservationValidator roomReservationValidator;
    private final RoomIdentifierValidator roomIdentifierValidator;
    private final IntervalValidator intervalValidator;
    private final Map<String, RoomReservations> roomReservationsMap = new ConcurrentHashMap<>();
    private final Striped<Lock> roomLocks = Striped.lock(1024);

    public RoomReservationResult bookRoom(RoomReservation roomReservation) {
        logger.info("Room reservation. roomReservation={}", roomReservation);

        roomReservationValidator.validate(roomReservation);

        // Normalize room id
        var roomId = roomReservation.roomId().trim();

        Lock lock = roomLocks.get(roomReservation.roomId());
        boolean acquired = false;
        try {
            acquired = lock.tryLock(60, TimeUnit.SECONDS);
            if (!acquired) {
                throw new IllegalStateException("Room '" + roomId + "' is busy; try again.");
            }

            var reservations = findOrCreateRoomReservations(roomReservation.roomId());
            return reservations.findCollision(roomReservation.reservationStart(), roomReservation.reservationEnd())
                    .map(collisionInterval -> {
                        logger.info("Room reservation has failed - collision detected. " +
                                        "roomId='{}', requestedInterval={}-{}, collisionInterval={}-{}",
                                roomId,
                                roomReservation.reservationStart(),
                                roomReservation.reservationEnd(),
                                collisionInterval.start(),
                                collisionInterval.end());
                        return new RoomReservationResult(
                                roomId,
                                collisionInterval.start(),
                                collisionInterval.end(),
                                false
                        );
                    }).orElseGet(() -> {
                        var reservation = reservations.add(
                                roomReservation.reservationStart(),
                                roomReservation.reservationEnd()
                        );

                        var result = new RoomReservationResult(
                                roomId,
                                reservation.start(),
                                reservation.end(),
                                true
                        );
                        logger.info("Room reservation has been successful. roomId='{}', requestedInterval={}-{}",
                                roomId,
                                roomReservation.reservationStart(),
                                roomReservation.reservationEnd());
                        return result;
                    });
        } catch (InterruptedException e) {
            throw new IllegalStateException("Interrupted while acquiring room lock", e);
        } finally {
            if (acquired) lock.unlock();
        }
    }

    @Override
    public Optional<String> findAvailableRoom(Instant reservationStart, Instant reservationEnd) {
        logger.info("Find available room. reservationStart={}, reservationEnd={}", reservationStart, reservationEnd);

        intervalValidator.validate(reservationStart, reservationEnd);

        var result = roomReservationsMap.entrySet().stream()
                .filter(e -> e.getValue().hasNoConflict(reservationStart, reservationEnd))
                .findFirst()
                .map(Map.Entry::getKey);

        result.ifPresentOrElse(
                roomId -> logger.info("Available room found. roomId='{}', reservationStart={}, reservationEnd={}",
                        roomId, reservationStart, reservationEnd),
                () -> logger.info("Available room not found. reservationStart={}, reservationEnd={}",
                        reservationStart, reservationEnd)
        );

        return result;
    }

    @Override
    public List<RoomReservationInfo> getReservations(String roomId) {
        logger.info("Get room reservations. roomId='{}'", roomId);

        roomIdentifierValidator.validate(roomId);

        var roomReservations = roomReservationsMap.get(roomId);
        if (roomReservations == null) {
            throw new IllegalArgumentException("Room '" + roomId + "' not found");
        }

        return roomReservations.asList().stream()
                .map(reservation ->
                        new RoomReservationInfo(
                                roomId,
                                reservation.start(),
                                reservation.end()
                        )
                )
                .toList();
    }

    @Override
    public Map<String, List<RoomReservationInfo>> getAllReservations() {
        logger.info("Get all room reservations");

        return roomReservationsMap.entrySet().stream()
                .map(e -> {
                            String roomId = e.getKey();
                            List<ReservationInterval> intervals = e.getValue().asList();
                            return Map.entry(
                                    roomId,
                                    intervals.stream()
                                            .map(reservation ->
                                                    new RoomReservationInfo(
                                                            roomId,
                                                            reservation.start(),
                                                            reservation.end()
                                                    )
                                            )
                                            .toList()
                            );
                        }
                )
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> b,
                        LinkedHashMap::new
                ));
    }

    /**
     * For testing purposes.
     */
    public void reset() {
        roomReservationsMap.clear();
    }

    private RoomReservations findOrCreateRoomReservations(String roomId) {
        logger.debug("Find or create new room reservations. roomId='{}'", roomId);
        return roomReservationsMap.computeIfAbsent(
                roomId,
                __ -> {
                    logger.info("Creating new room reservations. roomId='{}'", roomId);
                    return new RoomReservations();
                }
        );
    }
}
