package net.souvikcodes.KnowThisThings.dto.ExternalApiPojo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ElevenLabsRequestDto {
    @JsonProperty("text")
    private String text;

    @JsonProperty("model_id")
    private String modelId;
}
