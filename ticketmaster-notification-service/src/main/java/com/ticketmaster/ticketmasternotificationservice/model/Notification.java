package com.ticketmaster.ticketmasternotificatinservice.model;

import com.ticketmaster.ticketmasternotificatinservice.enums.NotificationStatus;
import com.ticketmaster.ticketmasternotificatinservice.enums.NotificationType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String recipient;

    @Enumerated(EnumType.STRING)
    private NotificationType type;
    @Enumerated(EnumType.STRING)
    private NotificationStatus status;

    private String subject;

    @Column(columnDefinition = "TEXT")
    private String message;

    private String errorMessage;

    private LocalDateTime created;
    private LocalDateTime sent;
    private LocalDateTime failed;
}
