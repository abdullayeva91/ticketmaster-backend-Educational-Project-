package com.ticketmaster.ticketmasteruserservice.model;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
@Data
@Entity
@Table(name = "user_profiles")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class UserProfile {

    @Id
    private Long id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String address;
    private String profilePictureUrl;
    private LocalDate birthDate;

    @OneToOne(fetch = FetchType.EAGER)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;
}