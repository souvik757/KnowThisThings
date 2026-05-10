package net.souvikcodes.KnowThisThings;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
//import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableMongoAuditing
// @EnableScheduling
@PropertySource("file:/Users/souvik/code/ExternalProperties/KnowThisThingsDev.properties")
public class KnowThisThingsApplication {

	public static void main(String[] args) {
		SpringApplication.run(KnowThisThingsApplication.class, args);
	}
}
