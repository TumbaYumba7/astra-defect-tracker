package ru.mirea.coursework.defecttracker.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mirea.coursework.defecttracker.domain.model.ProjectRecord;
import ru.mirea.coursework.defecttracker.domain.port.ProjectStore;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class ProjectService {
    private final ProjectStore projects;
    public ProjectService(ProjectStore projects) { this.projects = projects; }
    public List<ProjectRecord> list() { return projects.findAll(); }
    public ProjectRecord require(UUID id) {
        return projects.findById(id).orElseThrow(() -> new NotFoundException("Проект не найден"));
    }
    @Transactional
    public ProjectRecord create(String projectKey, String name, String gameTitle, String description) {
        String key = projectKey.trim().toUpperCase();
        if (projects.existsByKey(key)) throw new ConflictException("Ключ проекта уже занят");
        return projects.save(new ProjectRecord(UUID.randomUUID(), key, name.trim(), gameTitle.trim(),
                description.trim(), Instant.now()));
    }
}
