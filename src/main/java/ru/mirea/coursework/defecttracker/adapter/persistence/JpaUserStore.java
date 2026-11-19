package ru.mirea.coursework.defecttracker.adapter.persistence;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.mirea.coursework.defecttracker.adapter.persistence.entity.UserEntity;
import ru.mirea.coursework.defecttracker.adapter.persistence.repository.UserJpaRepository;
import ru.mirea.coursework.defecttracker.domain.model.UserRecord;
import ru.mirea.coursework.defecttracker.domain.port.UserStore;
import java.util.Optional;

@Repository
public class JpaUserStore implements UserStore {
    private final UserJpaRepository repository;
    public JpaUserStore(UserJpaRepository repository) { this.repository = repository; }
    @Override @Transactional(readOnly = true)
    public Optional<UserRecord> findByUsername(String username) { return repository.findByUsername(username).map(JpaUserStore::toRecord); }
    @Override @Transactional(readOnly = true)
    public boolean existsByUsername(String username) { return repository.existsByUsername(username); }
    @Override @Transactional
    public UserRecord save(UserRecord user) {
        return toRecord(repository.save(new UserEntity(user.id(), user.username(), user.passwordHash(),
                user.displayName(), user.role(), user.createdAt())));
    }
    static UserRecord toRecord(UserEntity entity) {
        return new UserRecord(entity.getId(), entity.getUsername(), entity.getPasswordHash(),
                entity.getDisplayName(), entity.getRole(), entity.getCreatedAt());
    }
}
