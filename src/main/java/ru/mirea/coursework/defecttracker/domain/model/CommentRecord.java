package ru.mirea.coursework.defecttracker.domain.model;

import java.time.Instant;

public record CommentRecord(Long id, Long issueId, String authorUsername, String body, Instant createdAt) { }
