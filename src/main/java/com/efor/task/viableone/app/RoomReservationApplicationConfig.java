package com.efor.task.viableone.app;

import com.efor.task.viableone.common.CommonConfig;
import com.efor.task.viableone.reservation.ReservationConfig;
import com.efor.task.viableone.reservation.controller.ReservationControllerConfig;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        CommonConfig.class,
        ReservationConfig.class,
        ReservationControllerConfig.class
})
public class RoomReservationApplicationConfig {
    @Bean
    Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
        return builder -> builder
                .modules(new JavaTimeModule())
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
}
