package com.ticketmaster.discoveryserverr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class DiscoveryServerrApplication {

    public static void main(String[] args) {
        SpringApplication.run(DiscoveryServerrApplication.class, args);
    }

}
