package net.souvikcodes.KnowThisThings;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing
public class KnowThisThingsApplication {

	public static void main(String[] args) {
		SpringApplication.run(KnowThisThingsApplication.class, args);
	}

}
