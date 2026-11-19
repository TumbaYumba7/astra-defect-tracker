package ru.mirea.coursework.defecttracker.domain.model;

import ru.mirea.coursework.defecttracker.domain.IssueStatus;
import java.time.Instant;

public record HistoryRecord(Long id, Long issueId, IssueStatus oldStatus, IssueStatus newStatus,
                            String actorUsername, Instant createdAt) { }
