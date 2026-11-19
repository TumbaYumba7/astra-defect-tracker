package ru.mirea.coursework.defecttracker.adapter.web.dto;

import ru.mirea.coursework.defecttracker.domain.model.HistoryRecord;
import java.time.Instant;

public record HistoryResponse(Long id, Long issueId, String oldStatus, String newStatus,
                              String actorUsername, Instant createdAt) {
    public static HistoryResponse from(HistoryRecord h) {
        return new HistoryResponse(h.id(), h.issueId(), h.oldStatus() == null ? null : h.oldStatus().name(),
                h.newStatus().name(), h.actorUsername(), h.createdAt());
    }
}
