package com.exxeta.ai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class AiAzureApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiAzureApplication.class, args);
    }

}
