package ru.mirea.coursework.defecttracker.adapter.web.dto;

import ru.mirea.coursework.defecttracker.domain.model.IssueRecord;
import java.time.Instant;
import java.util.UUID;

public record IssueResponse(Long id, String issueKey, UUID projectId, String title, String description,
                            String reproductionSteps, String expectedResult, String actualResult,
                            String gameVersion, String platform, String severity, String priority,
                            String status, String reporterUsername, String assigneeUsername,
                            Instant createdAt, Instant updatedAt) {
    public static IssueResponse from(IssueRecord i) {
        return new IssueResponse(i.id(), i.projectKey() + "-" + String.format("%05d", i.id()), i.projectId(),
                i.title(), i.description(), i.reproductionSteps(), i.expectedResult(), i.actualResult(),
                i.gameVersion(), i.platform(), i.severity().name(), i.priority().name(), i.status().name(),
                i.reporterUsername(), i.assigneeUsername(), i.createdAt(), i.updatedAt());
    }
}
