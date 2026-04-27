package com.ticketmaster.ticketmasteruserservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateProfileRequest {

    @NotBlank(message = "Ad boş qala bilməz")
    private String firstName;

    @NotBlank(message = "Soyad boş qala bilməz")
    private String lastName;

    @NotBlank(message = "Email boş ola bilməz")
    @Email(message = "Email formatı düzgün deyil")
    private String email;

    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Telefon nömrəsi düzgün deyil")
    private String phoneNumber;

    @NotBlank(message = "Ünvan boş ola bilməz")
    private String address;

    @Past(message = "Doğum tarixi keçmiş zaman olmalıdır")
    private LocalDate birthDate;
}
