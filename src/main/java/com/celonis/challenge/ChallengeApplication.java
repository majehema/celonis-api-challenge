package com.celonis.challenge;

import com.celonis.challenge.configuration.JobConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;


@SpringBootApplication
@Import(JobConfiguration.class)
public class ChallengeApplication {
    public static final String SAMPLE_PROJECT = "Generate sample project task";
    public static final String COUNT_FROM_X_TO_Y = "Count from X to Y";

    public static void main(String[] args) {
        SpringApplication.run(ChallengeApplication.class, args);
    }

}
