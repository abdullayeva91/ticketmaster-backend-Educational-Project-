package com.ticketmaster.ticketmasterPaymentService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class TicketmasterPaymentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TicketmasterPaymentServiceApplication.class, args);
    }

}
