package com.ticketmaster.ticketmasterticketservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableFeignClients
@EnableScheduling
@EnableDiscoveryClient
@EnableJpaRepositories(basePackages = "com.ticketmaster.ticketmasterticketservice.repository")
public class TicketmasterTicketServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TicketmasterTicketServiceApplication.class, args);
    }

}
