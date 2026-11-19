package ru.mirea.coursework.defecttracker.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.mirea.coursework.defecttracker.domain.UserRole;
import ru.mirea.coursework.defecttracker.domain.model.UserRecord;
import ru.mirea.coursework.defecttracker.domain.port.UserStore;
import java.time.Instant;
import java.util.UUID;

@Configuration
public class BootstrapAdminInitializer {
    @Bean
    ApplicationRunner bootstrapAdmin(UserStore users, PasswordEncoder encoder,
                                     @Value("${app.bootstrap.admin-username:}") String username,
                                     @Value("${app.bootstrap.admin-password:}") String password) {
        return args -> {
            if (username == null || username.isBlank() || password == null || password.isBlank()) return;
            String normalized = username.trim().toLowerCase();
            if (!users.existsByUsername(normalized)) {
                users.save(new UserRecord(UUID.randomUUID(), normalized, encoder.encode(password),
                        "Администратор", UserRole.ADMIN, Instant.now()));
            }
        };
    }
}
