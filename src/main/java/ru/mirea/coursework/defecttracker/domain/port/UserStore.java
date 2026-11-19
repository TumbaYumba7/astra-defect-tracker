package ru.mirea.coursework.defecttracker.domain.port;

import ru.mirea.coursework.defecttracker.domain.model.UserRecord;
import java.util.Optional;

public interface UserStore {
    Optional<UserRecord> findByUsername(String username);
    boolean existsByUsername(String username);
    UserRecord save(UserRecord user);
}
