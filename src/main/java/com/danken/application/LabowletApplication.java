package com.danken.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
/***
 * This class is the main entry point and nothing else.
 *
 */
@SpringBootApplication
@EnableAutoConfiguration
public class LabowletApplication {

    public static void main(String[] args) {
        SpringApplication.run(LabowletApplication.class, args);
    }

}
