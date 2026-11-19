package ru.mirea.coursework.defecttracker.adapter.persistence.entity;

import jakarta.persistence.*;
import ru.mirea.coursework.defecttracker.domain.IssueStatus;
import java.time.Instant;

@Entity
@Table(name = "issue_history")
public class HistoryEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "issue_id", nullable = false) private IssueEntity issue;
    @Enumerated(EnumType.STRING) @Column(name = "old_status", length = 24) private IssueStatus oldStatus;
    @Enumerated(EnumType.STRING) @Column(name = "new_status", nullable = false, length = 24) private IssueStatus newStatus;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "actor_id", nullable = false) private UserEntity actor;
    @Column(name = "created_at", nullable = false) private Instant createdAt;
    protected HistoryEntity() { }
    public HistoryEntity(IssueEntity issue, IssueStatus oldStatus, IssueStatus newStatus, UserEntity actor, Instant createdAt) {
        this.issue = issue; this.oldStatus = oldStatus; this.newStatus = newStatus; this.actor = actor; this.createdAt = createdAt;
    }
    public Long getId() { return id; }
    public IssueEntity getIssue() { return issue; }
    public IssueStatus getOldStatus() { return oldStatus; }
    public IssueStatus getNewStatus() { return newStatus; }
    public UserEntity getActor() { return actor; }
    public Instant getCreatedAt() { return createdAt; }
}
