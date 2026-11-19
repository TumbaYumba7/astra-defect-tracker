package ru.mirea.coursework.defecttracker.adapter.web;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.mirea.coursework.defecttracker.adapter.web.dto.*;
import ru.mirea.coursework.defecttracker.application.IssueService;
import ru.mirea.coursework.defecttracker.domain.IssuePriority;
import ru.mirea.coursework.defecttracker.domain.IssueSeverity;
import ru.mirea.coursework.defecttracker.domain.IssueStatus;
import java.util.List;
import java.util.UUID;

@RestController
public class IssueController {
    private final IssueService issues;
    public IssueController(IssueService issues) { this.issues = issues; }

    public record CreateIssueRequest(
            @NotBlank @Size(max = 180) String title,
            @NotBlank @Size(max = 8000) String description,
            @NotBlank @Size(max = 8000) String reproductionSteps,
            @NotBlank @Size(max = 4000) String expectedResult,
            @NotBlank @Size(max = 4000) String actualResult,
            @NotBlank @Size(max = 64) String gameVersion,
            @NotBlank @Size(max = 64) String platform,
            IssueSeverity severity,
            IssuePriority priority) { }
    public record StatusRequest(IssueStatus status) { }
    public record AssigneeRequest(@NotBlank String username) { }
    public record CommentRequest(@NotBlank @Size(max = 5000) String body) { }

    @GetMapping("/api/projects/{projectId}/issues")
    public List<IssueResponse> list(@PathVariable UUID projectId,
                                    @RequestParam(required = false) IssueStatus status,
                                    @RequestParam(required = false) IssuePriority priority,
                                    @RequestParam(required = false, name = "q") String query) {
        return issues.list(projectId, status, priority, query).stream().map(IssueResponse::from).toList();
    }

    @PostMapping("/api/projects/{projectId}/issues")
    @ResponseStatus(HttpStatus.CREATED)
    public IssueResponse create(@PathVariable UUID projectId, @Valid @RequestBody CreateIssueRequest request,
                                @AuthenticationPrincipal UserDetails principal) {
        IssueSeverity severity = request.severity() == null ? IssueSeverity.MEDIUM : request.severity();
        IssuePriority priority = request.priority() == null ? IssuePriority.NORMAL : request.priority();
        return IssueResponse.from(issues.create(projectId, principal.getUsername(), request.title(), request.description(),
                request.reproductionSteps(), request.expectedResult(), request.actualResult(), request.gameVersion(),
                request.platform(), severity, priority));
    }

    @GetMapping("/api/issues/{issueId}")
    public IssueResponse get(@PathVariable Long issueId) { return IssueResponse.from(issues.require(issueId)); }

    @PatchMapping("/api/issues/{issueId}/status")
    @PreAuthorize("hasAnyRole('ADMIN','DEVELOPER','TESTER')")
    public IssueResponse transition(@PathVariable Long issueId, @Valid @RequestBody StatusRequest request,
                                    @AuthenticationPrincipal UserDetails principal) {
        if (request.status() == null) throw new IllegalArgumentException("Не задано новое состояние");
        return IssueResponse.from(issues.transition(issueId, request.status(), principal.getUsername()));
    }

    @PatchMapping("/api/issues/{issueId}/assignee")
    @PreAuthorize("hasAnyRole('ADMIN','DEVELOPER')")
    public IssueResponse assign(@PathVariable Long issueId, @Valid @RequestBody AssigneeRequest request) {
        return IssueResponse.from(issues.assign(issueId, request.username()));
    }

    @GetMapping("/api/issues/{issueId}/comments")
    public List<CommentResponse> comments(@PathVariable Long issueId) {
        return issues.comments(issueId).stream().map(CommentResponse::from).toList();
    }

    @PostMapping("/api/issues/{issueId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponse addComment(@PathVariable Long issueId, @Valid @RequestBody CommentRequest request,
                                      @AuthenticationPrincipal UserDetails principal) {
        return CommentResponse.from(issues.addComment(issueId, principal.getUsername(), request.body()));
    }

    @GetMapping("/api/issues/{issueId}/history")
    public List<HistoryResponse> history(@PathVariable Long issueId) {
        return issues.history(issueId).stream().map(HistoryResponse::from).toList();
    }
}
