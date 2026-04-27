package com.ticketmaster.ticketmastereventservice.kafka.event;

import com.ticketmaster.ticketmastereventservice.kafka.producer.EventCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendEventCreated(EventCreatedEvent event) {
        kafkaTemplate.send("event-topic", event);
    }
}