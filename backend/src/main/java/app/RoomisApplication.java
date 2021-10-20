package app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RoomisApplication {
	public static void main(String[] args) {
		SpringApplication.run(RoomisApplication.class, args);
	}
}