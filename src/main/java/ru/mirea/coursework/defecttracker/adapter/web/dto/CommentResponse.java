package ru.mirea.coursework.defecttracker.adapter.web.dto;

import ru.mirea.coursework.defecttracker.domain.model.CommentRecord;
import java.time.Instant;

public record CommentResponse(Long id, Long issueId, String authorUsername, String body, Instant createdAt) {
    public static CommentResponse from(CommentRecord c) {
        return new CommentResponse(c.id(), c.issueId(), c.authorUsername(), c.body(), c.createdAt());
    }
}
