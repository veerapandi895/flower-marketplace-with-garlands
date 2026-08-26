package com.flowermarket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FlowerMarketplaceApplication {
    public static void main(String[] args) {
        SpringApplication.run(FlowerMarketplaceApplication.class, args);
    }
}
