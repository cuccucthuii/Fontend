package org.example.cinema_reservation_system;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableConfigurationProperties
public class CinemaReservationSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(CinemaReservationSystemApplication.class, args);
    }

}
