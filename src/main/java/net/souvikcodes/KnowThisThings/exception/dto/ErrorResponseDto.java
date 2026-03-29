package net.souvikcodes.KnowThisThings.exception.dto;

import java.time.LocalDateTime;
import java.util.Map;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ErrorResponseDto {
    private int status;
    private String message;
    private String path;
    private LocalDateTime timestamp ;
    private Map<String, String> validationErrors;
    public ErrorResponseDto(int status, String message, String path) {
        this.status = status;
        this.message = message;
        this.path = path;
        this.timestamp = LocalDateTime.now();
    }
    public ErrorResponseDto(int status, String message,Map<String, String> validationErrors) {
        this.status = status;
        this.message = message;
        this.timestamp = LocalDateTime.now();
        this.validationErrors = validationErrors;
    }
}
