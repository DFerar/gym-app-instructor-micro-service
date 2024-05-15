package com.gym.gymmicroservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class GymMicroserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GymMicroserviceApplication.class, args);
    }

}
