package net.souvikcodes.KnowThisThings.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JournalEntryDto {
    @NotBlank(message = "Title cannot be blank")
    private String title ;
    @NotBlank(message = "Content cannot be blank")
    private String content;
}
