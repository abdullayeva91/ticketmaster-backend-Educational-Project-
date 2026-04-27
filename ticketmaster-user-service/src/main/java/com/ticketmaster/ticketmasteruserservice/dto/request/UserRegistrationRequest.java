package com.ticketmaster.ticketmasteruserservice.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserRegistrationRequest {

    @NotBlank(message = "Istifadəçi adı boş ola bilməz")
    private String username;

    @NotBlank(message = "Email boş ola bilməz")
    @Email(message = "Email formatı düzgün deyil")
    private String email;

    @NotBlank(message = "Şifrə boş ola bilməz")
    @Size(min = 6, message = "Şifrə ən az 6 simvol olmalıdır")
    private String password;

    @NotBlank(message = "Ad boş ola bilməz")
    private String firstName;

    @NotBlank(message = "Soyad boş ola bilməz")
    private String lastName;

    @NotBlank
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Telefon nömrəsi düzgün deyil")
    private String phoneNumber;

    @Past(message = "Doğum tarixi keçmiş zaman olmalıdır")
    private LocalDate birthDate;
}
