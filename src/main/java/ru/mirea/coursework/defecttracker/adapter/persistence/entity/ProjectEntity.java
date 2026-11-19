package ru.mirea.coursework.defecttracker.adapter.persistence.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "project")
public class ProjectEntity {
    @Id private UUID id;
    @Column(name = "project_key", nullable = false, unique = true, length = 12) private String projectKey;
    @Column(nullable = false, length = 120) private String name;
    @Column(name = "game_title", nullable = false, length = 120) private String gameTitle;
    @Column(nullable = false, columnDefinition = "text") private String description;
    @Column(name = "created_at", nullable = false) private Instant createdAt;

    protected ProjectEntity() { }
    public ProjectEntity(UUID id, String projectKey, String name, String gameTitle, String description, Instant createdAt) {
        this.id = id; this.projectKey = projectKey; this.name = name;
        this.gameTitle = gameTitle; this.description = description; this.createdAt = createdAt;
    }
    public UUID getId() { return id; }
    public String getProjectKey() { return projectKey; }
    public String getName() { return name; }
    public String getGameTitle() { return gameTitle; }
    public String getDescription() { return description; }
    public Instant getCreatedAt() { return createdAt; }
}
