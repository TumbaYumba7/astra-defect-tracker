package ru.mirea.coursework.defecttracker.adapter.persistence;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.mirea.coursework.defecttracker.adapter.persistence.entity.CommentEntity;
import ru.mirea.coursework.defecttracker.adapter.persistence.repository.CommentJpaRepository;
import ru.mirea.coursework.defecttracker.adapter.persistence.repository.IssueJpaRepository;
import ru.mirea.coursework.defecttracker.adapter.persistence.repository.UserJpaRepository;
import ru.mirea.coursework.defecttracker.application.NotFoundException;
import ru.mirea.coursework.defecttracker.domain.model.CommentRecord;
import ru.mirea.coursework.defecttracker.domain.port.CommentStore;
import java.util.List;

@Repository
public class JpaCommentStore implements CommentStore {
    private final CommentJpaRepository comments;
    private final IssueJpaRepository issues;
    private final UserJpaRepository users;
    public JpaCommentStore(CommentJpaRepository comments, IssueJpaRepository issues, UserJpaRepository users) {
        this.comments = comments; this.issues = issues; this.users = users;
    }
    @Override @Transactional(readOnly = true)
    public List<CommentRecord> findByIssue(Long issueId) { return comments.findByIssue_IdOrderByCreatedAtAsc(issueId).stream().map(JpaCommentStore::toRecord).toList(); }
    @Override @Transactional
    public CommentRecord save(CommentRecord record) {
        var issue = issues.findById(record.issueId()).orElseThrow(() -> new NotFoundException("Дефект не найден"));
        var author = users.findByUsername(record.authorUsername()).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        return toRecord(comments.saveAndFlush(new CommentEntity(issue, author, record.body(), record.createdAt())));
    }
    private static CommentRecord toRecord(CommentEntity entity) {
        return new CommentRecord(entity.getId(), entity.getIssue().getId(), entity.getAuthor().getUsername(), entity.getBody(), entity.getCreatedAt());
    }
}
