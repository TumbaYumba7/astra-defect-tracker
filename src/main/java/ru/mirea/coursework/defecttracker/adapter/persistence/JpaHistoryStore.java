package ru.mirea.coursework.defecttracker.adapter.persistence;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.mirea.coursework.defecttracker.adapter.persistence.entity.HistoryEntity;
import ru.mirea.coursework.defecttracker.adapter.persistence.repository.HistoryJpaRepository;
import ru.mirea.coursework.defecttracker.adapter.persistence.repository.IssueJpaRepository;
import ru.mirea.coursework.defecttracker.adapter.persistence.repository.UserJpaRepository;
import ru.mirea.coursework.defecttracker.application.NotFoundException;
import ru.mirea.coursework.defecttracker.domain.model.HistoryRecord;
import ru.mirea.coursework.defecttracker.domain.port.HistoryStore;
import java.util.List;

@Repository
public class JpaHistoryStore implements HistoryStore {
    private final HistoryJpaRepository history;
    private final IssueJpaRepository issues;
    private final UserJpaRepository users;
    public JpaHistoryStore(HistoryJpaRepository history, IssueJpaRepository issues, UserJpaRepository users) {
        this.history = history; this.issues = issues; this.users = users;
    }
    @Override @Transactional(readOnly = true)
    public List<HistoryRecord> findByIssue(Long issueId) { return history.findByIssue_IdOrderByCreatedAtAsc(issueId).stream().map(JpaHistoryStore::toRecord).toList(); }
    @Override @Transactional
    public HistoryRecord save(HistoryRecord record) {
        var issue = issues.findById(record.issueId()).orElseThrow(() -> new NotFoundException("Дефект не найден"));
        var actor = users.findByUsername(record.actorUsername()).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        return toRecord(history.saveAndFlush(new HistoryEntity(issue, record.oldStatus(), record.newStatus(), actor, record.createdAt())));
    }
    private static HistoryRecord toRecord(HistoryEntity entity) {
        return new HistoryRecord(entity.getId(), entity.getIssue().getId(), entity.getOldStatus(), entity.getNewStatus(),
                entity.getActor().getUsername(), entity.getCreatedAt());
    }
}
