package com.efor.task.viableone.common;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        GlobalExceptionHandler.class
})
public class CommonConfig {
}
