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
public class DadJoke {
    @JsonProperty("joke")
    private String joke;
}
