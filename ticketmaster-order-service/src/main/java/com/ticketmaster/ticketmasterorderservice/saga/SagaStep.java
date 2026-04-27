package com.ticketmaster.ticketmasterorderservice.saga;

import lombok.Getter;

@Getter
public class SagaStep {
    private final String name;
    private final Runnable action;
    private final Runnable compensation;

    public SagaStep(String name, Runnable action, Runnable compensation) {
        this.name = name;
        this.action = action;
        this.compensation = compensation;
    }

    public void execute() { action.run(); }
    public void compensate() { compensation.run(); }
}