package com.kob.matchingsystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    //inject rest template component
    //when some components are required, just define a bean somewhere (e.g. in its configuration file) for injection
    @Bean
    public RestTemplate getRestTemplate(){
        return new RestTemplate();
    }
}
