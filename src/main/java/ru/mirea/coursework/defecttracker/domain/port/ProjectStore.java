package ru.mirea.coursework.defecttracker.domain.port;

import ru.mirea.coursework.defecttracker.domain.model.ProjectRecord;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectStore {
    List<ProjectRecord> findAll();
    Optional<ProjectRecord> findById(UUID id);
    boolean existsByKey(String projectKey);
    ProjectRecord save(ProjectRecord project);
}
