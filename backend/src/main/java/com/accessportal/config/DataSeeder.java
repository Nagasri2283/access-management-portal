package com.accessportal.config;

import com.accessportal.entity.Resource;
import com.accessportal.entity.Role;
import com.accessportal.entity.User;
import com.accessportal.repository.ResourceRepository;
import com.accessportal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ResourceRepository resourceRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedUsers();
        seedResources();
    }

    private void seedUsers() {
        if (userRepository.count() > 0) {
            return; // already seeded
        }

        userRepository.save(User.builder()
                .fullName("Ava Admin")
                .email("admin@company.com")
                .password(passwordEncoder.encode("admin123"))
                .department("IT")
                .role(Role.ADMIN)
                .build());

        userRepository.save(User.builder()
                .fullName("Mia Manager")
                .email("manager@company.com")
                .password(passwordEncoder.encode("manager123"))
                .department("Engineering")
                .role(Role.MANAGER)
                .build());

        userRepository.save(User.builder()
                .fullName("Ethan Employee")
                .email("employee@company.com")
                .password(passwordEncoder.encode("employee123"))
                .department("Engineering")
                .role(Role.EMPLOYEE)
                .build());

        System.out.println("Seeded demo accounts: admin@company.com / manager@company.com / employee@company.com (see README for passwords)");
    }

    private void seedResources() {
        if (resourceRepository.count() > 0) {
            return; // already seeded
        }

        resourceRepository.save(Resource.builder()
                .name("Production Database")
                .description("Read/write access to the production PostgreSQL cluster")
                .resourceType("Database")
                .build());

        resourceRepository.save(Resource.builder()
                .name("GitHub Admin Access")
                .description("Admin rights on the core engineering repositories")
                .resourceType("Repository")
                .build());

        resourceRepository.save(Resource.builder()
                .name("AWS Console (Staging)")
                .description("Console access to the staging AWS account")
                .resourceType("Cloud Console")
                .build());

        resourceRepository.save(Resource.builder()
                .name("VPN - Finance Network")
                .description("VPN access to the internal finance network segment")
                .resourceType("Network")
                .build());
    }
}
