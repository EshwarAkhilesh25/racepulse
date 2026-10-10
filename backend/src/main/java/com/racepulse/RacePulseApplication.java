package com.racepulse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RacePulseApplication {

  public static void main(String[] args) {
    SpringApplication.run(RacePulseApplication.class, args);
  }

}
