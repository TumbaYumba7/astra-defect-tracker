package ru.mirea.coursework.defecttracker.adapter.persistence;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.mirea.coursework.defecttracker.adapter.persistence.entity.ProjectEntity;
import ru.mirea.coursework.defecttracker.adapter.persistence.repository.ProjectJpaRepository;
import ru.mirea.coursework.defecttracker.domain.model.ProjectRecord;
import ru.mirea.coursework.defecttracker.domain.port.ProjectStore;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaProjectStore implements ProjectStore {
    private final ProjectJpaRepository repository;
    public JpaProjectStore(ProjectJpaRepository repository) { this.repository = repository; }
    @Override @Transactional(readOnly = true)
    public List<ProjectRecord> findAll() { return repository.findAllByOrderByProjectKeyAsc().stream().map(JpaProjectStore::toRecord).toList(); }
    @Override @Transactional(readOnly = true)
    public Optional<ProjectRecord> findById(UUID id) { return repository.findById(id).map(JpaProjectStore::toRecord); }
    @Override @Transactional(readOnly = true)
    public boolean existsByKey(String key) { return repository.existsByProjectKey(key); }
    @Override @Transactional
    public ProjectRecord save(ProjectRecord project) {
        return toRecord(repository.save(new ProjectEntity(project.id(), project.projectKey(), project.name(),
                project.gameTitle(), project.description(), project.createdAt())));
    }
    static ProjectRecord toRecord(ProjectEntity entity) {
        return new ProjectRecord(entity.getId(), entity.getProjectKey(), entity.getName(), entity.getGameTitle(),
                entity.getDescription(), entity.getCreatedAt());
    }
}
