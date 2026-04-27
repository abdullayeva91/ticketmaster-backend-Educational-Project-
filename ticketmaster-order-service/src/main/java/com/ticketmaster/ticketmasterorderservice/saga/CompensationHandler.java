package com.ticketmaster.ticketmasterorderservice.saga;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class CompensationHandler {

    public void compensate(List<SagaStep> completedSteps) {
        List<SagaStep> reversed = new ArrayList<>(completedSteps);
        java.util.Collections.reverse(reversed);

        for (SagaStep step : reversed) {
            try {
                log.info("Compensating step: {}", step.getName());
                step.compensate();
            } catch (Exception e) {
                log.error("Compensation failed for step: {}", step.getName());
            }
        }
    }
}