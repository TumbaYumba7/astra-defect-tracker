package ru.mirea.coursework.defecttracker.application;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mirea.coursework.defecttracker.domain.UserRole;
import ru.mirea.coursework.defecttracker.domain.model.UserRecord;
import ru.mirea.coursework.defecttracker.domain.port.UserStore;
import java.time.Instant;
import java.util.UUID;

@Service
public class UserService {
    private final UserStore users;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserStore users, PasswordEncoder passwordEncoder) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserRecord register(String username, String rawPassword, String displayName) {
        String normalized = username.trim().toLowerCase();
        if (users.existsByUsername(normalized)) throw new ConflictException("Имя пользователя уже занято");
        return users.save(new UserRecord(UUID.randomUUID(), normalized, passwordEncoder.encode(rawPassword),
                displayName.trim(), UserRole.REPORTER, Instant.now()));
    }

    public UserRecord require(String username) {
        return users.findByUsername(username).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }
}
