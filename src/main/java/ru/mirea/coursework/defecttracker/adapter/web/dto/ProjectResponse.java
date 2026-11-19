package ru.mirea.coursework.defecttracker.adapter.web.dto;

import ru.mirea.coursework.defecttracker.domain.model.ProjectRecord;
import java.time.Instant;
import java.util.UUID;

public record ProjectResponse(UUID id, String projectKey, String name, String gameTitle,
                              String description, Instant createdAt) {
    public static ProjectResponse from(ProjectRecord p) {
        return new ProjectResponse(p.id(), p.projectKey(), p.name(), p.gameTitle(), p.description(), p.createdAt());
    }
}
