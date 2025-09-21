package org.example.cinema_reservation_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableConfigurationProperties
@EnableScheduling
public class CinemaReservationSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(CinemaReservationSystemApplication.class, args);
    }

}
