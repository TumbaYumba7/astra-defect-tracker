package ru.mirea.coursework.defecttracker.domain.port;

import ru.mirea.coursework.defecttracker.domain.model.HistoryRecord;
import java.util.List;

public interface HistoryStore {
    List<HistoryRecord> findByIssue(Long issueId);
    HistoryRecord save(HistoryRecord history);
}
