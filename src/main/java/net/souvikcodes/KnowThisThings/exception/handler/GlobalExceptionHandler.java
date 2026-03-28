package net.souvikcodes.KnowThisThings.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import net.souvikcodes.KnowThisThings.exception.customexception.JournalEntryException;
import net.souvikcodes.KnowThisThings.exception.customexception.ResourceNotFoundException;
import net.souvikcodes.KnowThisThings.exception.dto.ErrorResponseDto;

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

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGeneric(Exception ex, HttpServletRequest req){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new ErrorResponseDto(500, ex.getMessage(), req.getRequestURI())) ;
    }
}
