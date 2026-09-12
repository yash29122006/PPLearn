package com.p2plearn.learning.config;

import com.p2plearn.learning.service.CompetitionService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CompetitionDataInitializer {

    @Bean
    CommandLineRunner createCurrentCompetition(
            CompetitionService competitionService
    ) {
        return args -> {

            competitionService
                    .getOrCreateCurrentCompetition();

            System.out.println(
                    "Current competition verified successfully."
            );
        };
    }
}