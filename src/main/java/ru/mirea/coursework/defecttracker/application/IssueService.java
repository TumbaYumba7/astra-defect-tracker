package ru.mirea.coursework.defecttracker.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mirea.coursework.defecttracker.domain.*;
import ru.mirea.coursework.defecttracker.domain.model.*;
import ru.mirea.coursework.defecttracker.domain.port.*;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class IssueService {
    private final IssueStore issues;
    private final ProjectStore projects;
    private final UserStore users;
    private final CommentStore comments;
    private final HistoryStore history;

    public IssueService(IssueStore issues, ProjectStore projects, UserStore users,
                        CommentStore comments, HistoryStore history) {
        this.issues = issues;
        this.projects = projects;
        this.users = users;
        this.comments = comments;
        this.history = history;
    }

    public List<IssueRecord> list(UUID projectId, IssueStatus status, IssuePriority priority, String query) {
        projects.findById(projectId).orElseThrow(() -> new NotFoundException("Проект не найден"));
        return issues.findByProject(projectId, status, priority, query == null || query.isBlank() ? null : query.trim());
    }

    public IssueRecord require(Long id) {
        return issues.findById(id).orElseThrow(() -> new NotFoundException("Дефект не найден"));
    }

    @Transactional
    public IssueRecord create(UUID projectId, String reporter, String title, String description,
                              String steps, String expected, String actual, String gameVersion,
                              String platform, IssueSeverity severity, IssuePriority priority) {
        ProjectRecord project = projects.findById(projectId)
                .orElseThrow(() -> new NotFoundException("Проект не найден"));
        UserRecord user = users.findByUsername(reporter)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Instant now = Instant.now();
        return issues.save(new IssueRecord(null, project.id(), project.projectKey(), title.trim(),
                description.trim(), steps.trim(), expected.trim(), actual.trim(), gameVersion.trim(),
                platform.trim(), severity, priority, IssueStatus.NEW, user.username(), null, now, now));
    }

    @Transactional
    public IssueRecord transition(Long issueId, IssueStatus target, String actor) {
        IssueRecord current = require(issueId);
        if (!IssueLifecycle.canTransition(current.status(), target)) {
            throw new ConflictException("Переход из " + current.status() + " в " + target + " не разрешён");
        }
        users.findByUsername(actor).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Instant now = Instant.now();
        IssueRecord updated = new IssueRecord(current.id(), current.projectId(), current.projectKey(),
                current.title(), current.description(), current.reproductionSteps(), current.expectedResult(),
                current.actualResult(), current.gameVersion(), current.platform(), current.severity(),
                current.priority(), target, current.reporterUsername(), current.assigneeUsername(), current.createdAt(), now);
        issues.save(updated);
        history.save(new HistoryRecord(null, issueId, current.status(), target, actor, now));
        return require(issueId);
    }

    @Transactional
    public IssueRecord assign(Long issueId, String targetUsername) {
        IssueRecord current = require(issueId);
        UserRecord assignee = users.findByUsername(targetUsername.trim().toLowerCase())
                .orElseThrow(() -> new NotFoundException("Исполнитель не найден"));
        if (assignee.role() != UserRole.ADMIN && assignee.role() != UserRole.DEVELOPER && assignee.role() != UserRole.TESTER) {
            throw new ConflictException("Исполнителем может быть разработчик или тестировщик");
        }
        IssueRecord updated = new IssueRecord(current.id(), current.projectId(), current.projectKey(),
                current.title(), current.description(), current.reproductionSteps(), current.expectedResult(),
                current.actualResult(), current.gameVersion(), current.platform(), current.severity(),
                current.priority(), current.status(), current.reporterUsername(), assignee.username(), current.createdAt(), Instant.now());
        return issues.save(updated);
    }

    public List<CommentRecord> comments(Long issueId) { require(issueId); return comments.findByIssue(issueId); }
    public List<HistoryRecord> history(Long issueId) { require(issueId); return history.findByIssue(issueId); }

    @Transactional
    public CommentRecord addComment(Long issueId, String author, String body) {
        require(issueId);
        users.findByUsername(author).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        return comments.save(new CommentRecord(null, issueId, author, body.trim(), Instant.now()));
    }
}
