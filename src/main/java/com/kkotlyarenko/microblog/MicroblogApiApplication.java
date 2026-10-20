package com.kkotlyarenko.microblog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Точка входа приложения — REST API микроблога.
 */
@SpringBootApplication
public class MicroblogApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(MicroblogApiApplication.class, args);
    }
}
