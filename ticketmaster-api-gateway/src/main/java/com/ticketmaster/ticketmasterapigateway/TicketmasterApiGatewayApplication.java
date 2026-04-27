package com.ticketmaster.ticketmasterapigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class TicketmasterApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(TicketmasterApiGatewayApplication.class, args);
    }

}
