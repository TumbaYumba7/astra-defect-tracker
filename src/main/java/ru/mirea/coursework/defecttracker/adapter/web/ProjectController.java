package ru.mirea.coursework.defecttracker.adapter.web;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.mirea.coursework.defecttracker.adapter.web.dto.ProjectResponse;
import ru.mirea.coursework.defecttracker.application.ProjectService;
import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {
    private final ProjectService projects;
    public ProjectController(ProjectService projects) { this.projects = projects; }

    public record CreateProjectRequest(
            @NotBlank @Pattern(regexp = "[A-Za-z][A-Za-z0-9]{1,11}") String projectKey,
            @NotBlank @Size(max = 120) String name,
            @NotBlank @Size(max = 120) String gameTitle,
            @NotBlank @Size(max = 2000) String description) { }

    @GetMapping
    public List<ProjectResponse> list() { return projects.list().stream().map(ProjectResponse::from).toList(); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public ProjectResponse create(@Valid @RequestBody CreateProjectRequest request) {
        return ProjectResponse.from(projects.create(request.projectKey(), request.name(), request.gameTitle(), request.description()));
    }
}
