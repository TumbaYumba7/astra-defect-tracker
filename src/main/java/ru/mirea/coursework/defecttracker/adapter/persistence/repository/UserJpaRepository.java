package ru.mirea.coursework.defecttracker.adapter.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mirea.coursework.defecttracker.adapter.persistence.entity.UserEntity;
import java.util.Optional;
import java.util.UUID;

public interface UserJpaRepository extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByUsername(String username);
    boolean existsByUsername(String username);
}
