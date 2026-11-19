package ru.mirea.coursework.defecttracker.domain.port;

import ru.mirea.coursework.defecttracker.domain.model.CommentRecord;
import java.util.List;

public interface CommentStore {
    List<CommentRecord> findByIssue(Long issueId);
    CommentRecord save(CommentRecord comment);
}
