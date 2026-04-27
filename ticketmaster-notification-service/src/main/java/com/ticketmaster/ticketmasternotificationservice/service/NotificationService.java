package com.ticketmaster.ticketmasternotificatinservice.service;

import com.ticketmaster.ticketmasternotificatinservice.enums.NotificationStatus;
import com.ticketmaster.ticketmasternotificatinservice.enums.NotificationType;
import com.ticketmaster.ticketmasternotificatinservice.event.*;
import com.ticketmaster.ticketmasternotificatinservice.model.Notification;
import com.ticketmaster.ticketmasternotificatinservice.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final EmailService emailService;

    public void processPaymentSuccess(PaymentSuccessEvent event) {
        log.info("Kafka-dan uğurlu ödəniş xəbəri gəldi. Sifariş ID: {}", event.orderId());

        String subject = "TicketMaster - Uğurlu Ödəniş";
        String text = String.format("Təbriklər! %d nömrəli sifarişiniz təsdiqləndi. Ödənilən məbləğ: %s AZN. Biletiniz hazırdır!",
                event.orderId(), event.amount());
        Notification notification = Notification.builder()
                .userId(event.userId())
                .recipient(event.userEmail())
                .type(NotificationType.EMAIL)
                .status(NotificationStatus.PENDING)
                .subject(subject)
                .message(text)
                .created(LocalDateTime.now())
                .build();

        Notification savedNotification = notificationRepository.save(notification);

        try {
            emailService.sendEmail(event.userEmail(), subject, text);

            savedNotification.setStatus(NotificationStatus.SENT);
            savedNotification.setSent(LocalDateTime.now());
            notificationRepository.save(savedNotification);
            log.info("Bildiriş müştəriyə uğurla göndərildi. Bildiriş ID: {}", savedNotification.getId());

        } catch (Exception e) {
            savedNotification.setStatus(NotificationStatus.FAILED);
            savedNotification.setFailed(LocalDateTime.now());
            savedNotification.setErrorMessage(e.getMessage());
            notificationRepository.save(savedNotification);
            log.error("Bildiriş göndərilə bilmədi! Səbəb: {}", e.getMessage());
        }
    }
    public void processPaymentFailed(PaymentFailedEvent event) {
        log.info("Kafka-dan uğursuz ödəniş xəbəri gəldi. Sifariş ID: {}", event.orderId());
        String subject = "TicketMaster - Ödəniş Alınmadı ";
        String text = String.format("Hörmətli müştəri, %d nömrəli sifarişiniz üçün ödəniş uğursuz oldu. \nSəbəb: %s. \nXahiş edirik, başqa bir kartla yenidən cəhd edin.",
                event.orderId(), event.reason());
        Notification notification = Notification.builder()
                .userId(event.userId())
                .recipient(event.userEmail())
                .type(NotificationType.EMAIL)
                .status(NotificationStatus.PENDING)
                .subject(subject)
                .message(text)
                .created(LocalDateTime.now())
                .build();
        Notification savedNotification = notificationRepository.save(notification);
        try {
            emailService.sendEmail(event.userEmail(), subject, text);

            savedNotification.setStatus(NotificationStatus.SENT);
            savedNotification.setSent(LocalDateTime.now());

            notificationRepository.save(savedNotification);
            log.info("Uğursuz ödəniş bildirişi müştəriyə göndərildi. Bildiriş ID: {}", savedNotification.getId());

        } catch (Exception e) {
            savedNotification.setStatus(NotificationStatus.FAILED);
            savedNotification.setFailed(LocalDateTime.now());
            savedNotification.setErrorMessage(e.getMessage());

            notificationRepository.save(savedNotification);
            log.error("Bildiriş göndərilə bilmədi! Səbəb: {}", e.getMessage());
        }
    }
    public void processUserRegistration(UserRegisteredEvent event) {
        log.info("Kafka-dan qeydiyyat xəbəri gəldi. Email: {}", event.email());
        String subject = "TicketMaster - Qeydiyyatınız Uğurla Tamamlandı! 🎉";
        String text = String.format("Xoş gəldiniz, %s! TicketMaster ailəsinə qatıldığınız üçün təşəkkür edirik. Hesabınız uğurla yaradıldı.",
                event.fullName());

        Notification notification = Notification.builder()
                .userId(event.userId())
                .recipient(event.email())
                .type(NotificationType.EMAIL)
                .status(NotificationStatus.PENDING)
                .subject(subject)
                .message(text)
                .created(LocalDateTime.now())
                .build();

        Notification savedNotification = notificationRepository.save(notification);

        try {
            emailService.sendEmail(event.email(), subject, text);

            savedNotification.setStatus(NotificationStatus.SENT);
            savedNotification.setSent(LocalDateTime.now());
            notificationRepository.save(savedNotification);
            log.info("Qeydiyyat bildirişi uğurla göndərildi: {}", savedNotification.getId());

        } catch (Exception e) {
            savedNotification.setStatus(NotificationStatus.FAILED);
            savedNotification.setFailed(LocalDateTime.now());
            savedNotification.setErrorMessage(e.getMessage());
            notificationRepository.save(savedNotification);
            log.error("Qeydiyyat bildirişi göndərilə bilmədi: {}", e.getMessage());
        }
    }

    public void processOrderCreated(OrderCreatedEvent event) {
        log.info("Kafka-dan sifariş xəbəri gəldi. OrderId: {}", event.orderId());

        String subject = "TicketMaster - Sifarişiniz Qəbul Edildi 🎫";
        String text = String.format("Hörmətli müştəri, %d nömrəli sifarişiniz uğurla qeydə alındı. Ümumi məbləğ: %s AZN. Ödəniş mərhələsini gözləyirik.",
                event.orderId(), event.totalAmount());

        Notification notification = Notification.builder()
                .userId(event.userId())
                .recipient(event.userEmail())
                .type(NotificationType.EMAIL)
                .status(NotificationStatus.PENDING)
                .subject(subject)
                .message(text)
                .created(LocalDateTime.now())
                .build();

        Notification savedNotification = notificationRepository.save(notification);

        try {
            emailService.sendEmail(event.userEmail(), subject, text);

            savedNotification.setStatus(NotificationStatus.SENT);
            savedNotification.setSent(LocalDateTime.now());
            notificationRepository.save(savedNotification);
            log.info("Sifariş bildirişi uğurla göndərildi: {}", savedNotification.getId());

        } catch (Exception e) {
            savedNotification.setStatus(NotificationStatus.FAILED);
            savedNotification.setFailed(LocalDateTime.now());
            savedNotification.setErrorMessage(e.getMessage());
            notificationRepository.save(savedNotification);
            log.error("Sifariş bildirişi göndərilə bilmədi: {}", e.getMessage());
        }
    }
    public void processForgotPassword(ForgotPasswordEvent event) {
        log.info("Kafka-dan şifrə sıfırlama xəbəri gəldi. Email: {}", event.email());

        String subject = "TicketMaster - Şifrə Sıfırlama Kodu 🔐";
        String text = String.format("Hörmətli istifadəçi, şifrənizi sıfırlamaq üçün kodunuz: %s. \nBu kod 15 dəqiqə ərzində keçərlidir.",
                event.token());

        Notification notification = Notification.builder()
                .recipient(event.email())
                .type(NotificationType.EMAIL)
                .status(NotificationStatus.PENDING)
                .subject(subject)
                .message(text)
                .created(LocalDateTime.now())
                .build();

        Notification savedNotification = notificationRepository.save(notification);

        try {
            emailService.sendEmail(event.email(), subject, text);

            savedNotification.setStatus(NotificationStatus.SENT);
            savedNotification.setSent(LocalDateTime.now());
            notificationRepository.save(savedNotification);
            log.info("Şifrə sıfırlama kodu uğurla göndərildi: {}", savedNotification.getId());

        } catch (Exception e) {
            savedNotification.setStatus(NotificationStatus.FAILED);
            savedNotification.setFailed(LocalDateTime.now());
            savedNotification.setErrorMessage(e.getMessage());
            notificationRepository.save(savedNotification);
            log.error("Şifrə sıfırlama bildirişi göndərilə bilmədi: {}", e.getMessage());
        }
    }
    public void processPasswordChanged(PasswordChangedEvent event) {
        log.info("Şifrə yenilənmə emaili hazırlanır. Email: {}", event.email());

        String subject = "TicketMaster - Şifrəniz Yeniləndi ✅";
        String text = "Hörmətli istifadəçi,\n\n" +
                "Hesabınızın şifrəsi uğurla yeniləndi. \n" +
                "Əgər bu dəyişikliyi siz etməmisinizsə, xahiş edirik dərhal bizimlə əlaqə saxlayın.\n\n" +
                "Hörmətlə,\nTicketMaster Komandası";

        Notification notification = Notification.builder()
                .recipient(event.email())
                .type(NotificationType.EMAIL)
                .status(NotificationStatus.PENDING)
                .subject(subject)
                .message(text)
                .created(LocalDateTime.now())
                .build();

        Notification savedNotification = notificationRepository.save(notification);

        try {
            emailService.sendEmail(event.email(), subject, text);

            savedNotification.setStatus(NotificationStatus.SENT);
            savedNotification.setSent(LocalDateTime.now());
            notificationRepository.save(savedNotification);
            log.info("Şifrə yenilənmə təsdiq maili uğurla göndərildi: {}", savedNotification.getId());

        } catch (Exception e) {
            savedNotification.setStatus(NotificationStatus.FAILED);
            savedNotification.setFailed(LocalDateTime.now());
            savedNotification.setErrorMessage(e.getMessage());
            notificationRepository.save(savedNotification);
            log.error("Şifrə yenilənmə bildirişi göndərilə bilmədi: {}", e.getMessage());
        }
    }
}