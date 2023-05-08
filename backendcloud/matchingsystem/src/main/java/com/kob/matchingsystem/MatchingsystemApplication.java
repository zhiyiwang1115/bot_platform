package com.kob.matchingsystem;

import com.kob.matchingsystem.service.impl.MatchingServiceImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MatchingsystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(MatchingsystemApplication.class, args);
        //when running springboot application, running the process
        //not sure why it needs to be put after spring application starting
        MatchingServiceImpl.matchingPool.run();
    }

}
