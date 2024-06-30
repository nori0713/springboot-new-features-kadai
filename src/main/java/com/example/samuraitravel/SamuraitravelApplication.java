package com.example.samuraitravel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.example.samuraitravel.repository")
public class SamuraitravelApplication {

    public static void main(String[] args) {
        SpringApplication.run(SamuraitravelApplication.class, args);
    }
}