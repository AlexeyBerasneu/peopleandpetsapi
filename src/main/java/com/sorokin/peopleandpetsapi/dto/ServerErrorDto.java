package com.sorokin.peopleandpetsapi.dto;

import java.time.LocalDateTime;

public record ServerErrorDto (
        String message,
        String detailMessage,
        LocalDateTime dateTime
){
}
