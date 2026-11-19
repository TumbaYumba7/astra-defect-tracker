package ru.mirea.coursework.defecttracker.adapter.persistence.entity;

import jakarta.persistence.*;
import ru.mirea.coursework.defecttracker.domain.*;
import java.time.Instant;

@Entity
@Table(name = "issue")
public class IssueEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "project_id", nullable = false) private ProjectEntity project;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "reporter_id", nullable = false) private UserEntity reporter;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "assignee_id") private UserEntity assignee;
    @Column(nullable = false, length = 180) private String title;
    @Column(nullable = false, columnDefinition = "text") private String description;
    @Column(name = "reproduction_steps", nullable = false, columnDefinition = "text") private String reproductionSteps;
    @Column(name = "expected_result", nullable = false, columnDefinition = "text") private String expectedResult;
    @Column(name = "actual_result", nullable = false, columnDefinition = "text") private String actualResult;
    @Column(name = "game_version", nullable = false, length = 64) private String gameVersion;
    @Column(nullable = false, length = 64) private String platform;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 16) private IssueSeverity severity;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 16) private IssuePriority priority;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 24) private IssueStatus status;
    @Column(name = "created_at", nullable = false) private Instant createdAt;
    @Column(name = "updated_at", nullable = false) private Instant updatedAt;
    @Version @Column(name = "row_version", nullable = false) private long version;

    public IssueEntity() { }
    public Long getId() { return id; }
    public ProjectEntity getProject() { return project; }
    public UserEntity getReporter() { return reporter; }
    public UserEntity getAssignee() { return assignee; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getReproductionSteps() { return reproductionSteps; }
    public String getExpectedResult() { return expectedResult; }
    public String getActualResult() { return actualResult; }
    public String getGameVersion() { return gameVersion; }
    public String getPlatform() { return platform; }
    public IssueSeverity getSeverity() { return severity; }
    public IssuePriority getPriority() { return priority; }
    public IssueStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setProject(ProjectEntity value) { project = value; }
    public void setReporter(UserEntity value) { reporter = value; }
    public void setAssignee(UserEntity value) { assignee = value; }
    public void setTitle(String value) { title = value; }
    public void setDescription(String value) { description = value; }
    public void setReproductionSteps(String value) { reproductionSteps = value; }
    public void setExpectedResult(String value) { expectedResult = value; }
    public void setActualResult(String value) { actualResult = value; }
    public void setGameVersion(String value) { gameVersion = value; }
    public void setPlatform(String value) { platform = value; }
    public void setSeverity(IssueSeverity value) { severity = value; }
    public void setPriority(IssuePriority value) { priority = value; }
    public void setStatus(IssueStatus value) { status = value; }
    public void setCreatedAt(Instant value) { createdAt = value; }
    public void setUpdatedAt(Instant value) { updatedAt = value; }
}
