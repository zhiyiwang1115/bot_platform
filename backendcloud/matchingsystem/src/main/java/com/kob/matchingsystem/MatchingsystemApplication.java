package com.kob.matchingsystem;

import com.kob.matchingsystem.service.impl.MatchingServiceImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MatchingsystemApplication {

    public static void main(String[] args) {
        //when running springboot application, running the process
        MatchingServiceImpl.matchingPool.run();
        SpringApplication.run(MatchingsystemApplication.class, args);
    }

}
