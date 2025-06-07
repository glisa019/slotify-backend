package com.myslotify.slotify;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SlotifyApplication {

	public static void main(String[] args) {
		SpringApplication.run(SlotifyApplication.class, args);
	}

}
