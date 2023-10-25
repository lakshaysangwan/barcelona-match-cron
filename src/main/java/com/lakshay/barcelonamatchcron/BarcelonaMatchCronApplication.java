package com.lakshay.barcelonamatchcron;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@Log4j2
@EnableScheduling
@SpringBootApplication
public class BarcelonaMatchCronApplication {


    public static void main(String[] args) {
        SpringApplication.run(BarcelonaMatchCronApplication.class, args);
    }

}
