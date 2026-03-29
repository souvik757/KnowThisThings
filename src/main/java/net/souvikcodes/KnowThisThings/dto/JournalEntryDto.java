package net.souvikcodes.KnowThisThings.dto;

import org.hibernate.validator.constraints.UniqueElements;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JournalEntryDto {
    @NotBlank(message = "Title cannot be blank")
    @Size(max = 50, message = "Title cannot exceed 50 characters")
    private String title ;
    @NotBlank(message = "Content cannot be blank")
    @Size(max = 1000, message = "Content cannot exceed 1000 characters")
    @UniqueElements(message = "Same Content already exists")
    private String content;
}
