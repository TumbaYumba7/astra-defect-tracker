package ru.mirea.coursework.defecttracker.adapter.persistence.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "issue_comment")
public class CommentEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "issue_id", nullable = false) private IssueEntity issue;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "author_id", nullable = false) private UserEntity author;
    @Column(nullable = false, length = 5000) private String body;
    @Column(name = "created_at", nullable = false) private Instant createdAt;
    protected CommentEntity() { }
    public CommentEntity(IssueEntity issue, UserEntity author, String body, Instant createdAt) {
        this.issue = issue; this.author = author; this.body = body; this.createdAt = createdAt;
    }
    public Long getId() { return id; }
    public IssueEntity getIssue() { return issue; }
    public UserEntity getAuthor() { return author; }
    public String getBody() { return body; }
    public Instant getCreatedAt() { return createdAt; }
}
