package net.souvikcodes.KnowThisThings.exception.handler;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import net.souvikcodes.KnowThisThings.exception.customexception.JournalEntryException;
import net.souvikcodes.KnowThisThings.exception.customexception.ResourceNotFoundException;
import net.souvikcodes.KnowThisThings.exception.dto.ErrorResponseDto;

import org.springframework.web.bind.MethodArgumentNotValidException;
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleNotFound(ResourceNotFoundException ex, HttpServletRequest req){
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(new ErrorResponseDto(404, ex.getMessage(), req.getRequestURI())) ;
    }

    @ExceptionHandler(JournalEntryException.class)
    public ResponseEntity<ErrorResponseDto> handleJournalEntry(JournalEntryException ex, HttpServletRequest req){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponseDto(400, ex.getMessage(), req.getRequestURI())) ;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidation(MethodArgumentNotValidException ex){
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
        .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponseDto(400, "Validation errors found", errors)) ;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGeneric(Exception ex, HttpServletRequest req){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new ErrorResponseDto(500, ex.getMessage(), req.getRequestURI())) ;
    }
}
