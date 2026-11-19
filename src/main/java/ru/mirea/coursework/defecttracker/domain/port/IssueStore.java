package ru.mirea.coursework.defecttracker.domain.port;

import ru.mirea.coursework.defecttracker.domain.IssuePriority;
import ru.mirea.coursework.defecttracker.domain.IssueStatus;
import ru.mirea.coursework.defecttracker.domain.model.IssueRecord;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IssueStore {
    List<IssueRecord> findByProject(UUID projectId, IssueStatus status, IssuePriority priority, String query);
    Optional<IssueRecord> findById(Long id);
    IssueRecord save(IssueRecord issue);
}
