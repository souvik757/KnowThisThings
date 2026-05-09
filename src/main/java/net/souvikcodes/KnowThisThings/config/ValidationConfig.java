package net.souvikcodes.KnowThisThings.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import net.souvikcodes.KnowThisThings.Util.CustomValidation.Valid;

@Configuration
public class ValidationConfig {

    @Bean
    public Valid valid() {
        return new Valid();
    }
}
