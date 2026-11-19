package ru.mirea.coursework.defecttracker.adapter.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mirea.coursework.defecttracker.adapter.persistence.entity.ProjectEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectJpaRepository extends JpaRepository<ProjectEntity, UUID> {
    List<ProjectEntity> findAllByOrderByProjectKeyAsc();
    Optional<ProjectEntity> findByProjectKey(String projectKey);
    boolean existsByProjectKey(String projectKey);
}
