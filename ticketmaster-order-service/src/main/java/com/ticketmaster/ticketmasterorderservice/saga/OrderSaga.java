package com.ticketmaster.ticketmasterorderservice.saga;

import com.ticketmaster.ticketmasterorderservice.client.TicketServiceClient;
import com.ticketmaster.ticketmasterorderservice.dto.request.ReserveTicketRequest;
import com.ticketmaster.ticketmasterorderservice.enums.OrderStatus;
import com.ticketmaster.ticketmasterorderservice.model.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderSaga {

    private final TicketServiceClient ticketClient;
    private final CompensationHandler compensationHandler;

    public void execute(Order order) {
        List<SagaStep> steps = List.of(
                new SagaStep(
                        "RESERVE_TICKETS",
                        () -> {
                            log.info("Ticket Service-ə rezervasiya sorğusu: Order ID: {}, Biletlər: {}",
                                    order.getId(), order.getTicketIds());

                            // DTO-nu biletlərin siyahısı ilə doldururuq
                            ReserveTicketRequest request = ReserveTicketRequest.builder()
                                    .ticketIds(order.getTicketIds())
                                    .userId(order.getUserId())
                                    .build();

                            ticketClient.reserveTicket(request);
                        },
                        // Kompensasiya (Rollback): Əgər xəta olarsa, siyahıdakı hər bir bileti ləğv edirik
                        () -> {
                            if (order.getTicketIds() != null) {
                                order.getTicketIds().forEach(ticketClient::cancelReservation);
                            }
                        }
                )
        );

        executeSteps(steps, order);
    }

    private void executeSteps(List<SagaStep> steps, Order order) {
        List<SagaStep> completed = new ArrayList<>();

        for (SagaStep step : steps) {
            try {
                log.info("Saga addımı icra olunur: {}", step.getName());
                step.execute();
                completed.add(step);
            } catch (Exception e) {
                log.error("SAGA FAILED! Addım: {} | Xəta: {}", step.getName(), e.getMessage());

                // Geri qaytarma (Compensate)
                compensationHandler.compensate(completed);

                order.setStatus(OrderStatus.FAILED);
                order.setFailureReason(e.getMessage());
                return;
            }
        }

        // Hər şey uğurludursa
        order.setStatus(OrderStatus.CONFIRMED);
        log.info("Saga uğurla bitdi. Order ID: {} statusu CONFIRMED oldu.", order.getId());
    }
}