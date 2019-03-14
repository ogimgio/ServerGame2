package ch.uzh.ifi.seal.soprafs19.controller;

import ch.uzh.ifi.seal.soprafs19.exceptions.ConflictException;
import ch.uzh.ifi.seal.soprafs19.exceptions.NotFoundException;
import ch.uzh.ifi.seal.soprafs19.exceptions.UnauthorizedException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = ConflictException.class)
    protected ResponseEntity<Object> handleConflict(RuntimeException ex, WebRequest request) {
        String bodyOfResponse = ex.getMessage();
        HttpHeaders headers = new HttpHeaders();
        headers.add("reason", "Conflict");
        return handleExceptionInternal(ex, bodyOfResponse,
                headers, HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler(value = NotFoundException.class)
    protected ResponseEntity<Object> handleNotFound(RuntimeException ex, WebRequest request) {
        String bodyOfResponse = ex.getMessage();
        HttpHeaders headers = new HttpHeaders();
        headers.add("reason", "Not Found");
        return handleExceptionInternal(ex, bodyOfResponse,
                headers, HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(value = UnauthorizedException.class)
    protected ResponseEntity<Object> handleUnauthorized(RuntimeException ex, WebRequest request) {
        String bodyOfResponse = ex.getMessage();
        HttpHeaders headers = new HttpHeaders();
        headers.add("reason", "Unauthorized");
        return handleExceptionInternal(ex, bodyOfResponse,
                headers, HttpStatus.UNAUTHORIZED, request);
    }
}
