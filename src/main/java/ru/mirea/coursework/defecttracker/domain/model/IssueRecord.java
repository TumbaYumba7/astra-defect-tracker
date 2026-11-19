package ru.mirea.coursework.defecttracker.domain.model;

import ru.mirea.coursework.defecttracker.domain.IssuePriority;
import ru.mirea.coursework.defecttracker.domain.IssueSeverity;
import ru.mirea.coursework.defecttracker.domain.IssueStatus;
import java.time.Instant;
import java.util.UUID;

public record IssueRecord(Long id, UUID projectId, String projectKey, String title, String description,
                          String reproductionSteps, String expectedResult, String actualResult,
                          String gameVersion, String platform, IssueSeverity severity,
                          IssuePriority priority, IssueStatus status, String reporterUsername,
                          String assigneeUsername, Instant createdAt, Instant updatedAt) { }
