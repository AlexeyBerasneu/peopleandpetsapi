package com.sorokin.peopleandpetsapi.util;

import com.sorokin.peopleandpetsapi.dto.ServerErrorDto;
import com.sorokin.peopleandpetsapi.exception.PetNotFoundException;
import com.sorokin.peopleandpetsapi.exception.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    private Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ServerErrorDto> handleValidationException(MethodArgumentNotValidException ex) {
        log.error("Got validation exception", ex);
        String detailMessage = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error-> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ServerErrorDto(
                        "Validation Error",
                        detailMessage ,
                        LocalDateTime.now()
                ));
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ServerErrorDto> handleHandlerMethodValidationException(
            HandlerMethodValidationException ex
    ) {
        log.error("Got handler method validation exception", ex);
        String detailMessage = ex.getAllErrors().stream()
                .map(error -> {
                    if (error instanceof FieldError fieldError) {
                        return fieldError.getField() + ": " + fieldError.getDefaultMessage();
                    }
                    return error.getDefaultMessage();
                })
                .collect(Collectors.joining(", "));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ServerErrorDto(
                        "Validation Error",
                        detailMessage,
                        LocalDateTime.now()
                ));
    }


    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ServerErrorDto> handleUserNotFoundException(UserNotFoundException e) {
        log.error("User not found: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ServerErrorDto(
                        "User not found",
                        e.getMessage(),
                        LocalDateTime.now()
                )
        );
    }

    @ExceptionHandler(PetNotFoundException.class)
    public ResponseEntity<ServerErrorDto> handleUserNotFoundException(PetNotFoundException e) {
        log.error("Pet not found: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ServerErrorDto(
                        "Pet not found",
                        e.getMessage(),
                        LocalDateTime.now()
                )
        );
    }

    @ExceptionHandler
    public ResponseEntity<ServerErrorDto> handleGenericException(Exception ex) {
        log.error("Got exception", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ServerErrorDto(
                        "Server Error",
                        ex.getMessage(),
                        LocalDateTime.now()
                ));
    }
}
