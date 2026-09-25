package ru.bright.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    Map<String, Object> validation() {
        return Map.of(
                "error", "validation_failed",
                "message", "Request fields are invalid");
    }

    @ExceptionHandler({IllegalArgumentException.class, HandlerMethodValidationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    Map<String, Object> badRequest(RuntimeException exception) {
        return error("bad_request", exception);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    Map<String, Object> notFound(ResourceNotFoundException exception) {
        return error("not_found", exception);
    }

    @ExceptionHandler(ResourceConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    Map<String, Object> conflict(ResourceConflictException exception) {
        return error("conflict", exception);
    }

    @ExceptionHandler(StorageUnavailableException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    Map<String, Object> unavailable(StorageUnavailableException exception) {
        return error("storage_unavailable", exception);
    }

    private Map<String, Object> error(String code, RuntimeException exception) {
        return Map.of(
                "error", code,
                "message", exception.getMessage());
    }
}
