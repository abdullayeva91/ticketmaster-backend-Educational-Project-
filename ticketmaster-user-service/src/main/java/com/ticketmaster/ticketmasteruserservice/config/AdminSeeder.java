package com.ticketmaster.ticketmasteruserservice.config;

import com.ticketmaster.ticketmasteruserservice.enums.Role;
import com.ticketmaster.ticketmasteruserservice.model.User;
import com.ticketmaster.ticketmasteruserservice.model.UserProfile;
import com.ticketmaster.ticketmasteruserservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        String adminEmail = "admin@ticketmaster.com";

        if (!userRepository.existsByEmail(adminEmail)) {
            User admin = new User();
            admin.setEmail(adminEmail);
            admin.setUsername("superadmin");
            admin.setPassword(passwordEncoder.encode("admin12345"));
            admin.setRole(Role.ADMIN);

            UserProfile profile = new UserProfile();
            profile.setFirstName("Super");
            profile.setLastName("Admin");
            profile.setPhoneNumber("000-000-00-00");

            profile.setUser(admin);
            admin.setUserProfile(profile);

            userRepository.save(admin);

            System.out.println("✅ [SEEDER]: Super Admin və Profili uğurla yaradıldı.");
        } else {
            System.out.println("ℹ️ [SEEDER]: Admin artıq bazada mövcuddur.");
        }
    }
}