package ru.mirea.coursework.defecttracker.adapter.persistence;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.mirea.coursework.defecttracker.adapter.persistence.entity.IssueEntity;
import ru.mirea.coursework.defecttracker.adapter.persistence.entity.ProjectEntity;
import ru.mirea.coursework.defecttracker.adapter.persistence.entity.UserEntity;
import ru.mirea.coursework.defecttracker.adapter.persistence.repository.IssueJpaRepository;
import ru.mirea.coursework.defecttracker.adapter.persistence.repository.ProjectJpaRepository;
import ru.mirea.coursework.defecttracker.adapter.persistence.repository.UserJpaRepository;
import ru.mirea.coursework.defecttracker.application.NotFoundException;
import ru.mirea.coursework.defecttracker.domain.IssuePriority;
import ru.mirea.coursework.defecttracker.domain.IssueStatus;
import ru.mirea.coursework.defecttracker.domain.model.IssueRecord;
import ru.mirea.coursework.defecttracker.domain.port.IssueStore;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaIssueStore implements IssueStore {
    private final IssueJpaRepository repository;
    private final ProjectJpaRepository projects;
    private final UserJpaRepository users;
    public JpaIssueStore(IssueJpaRepository repository, ProjectJpaRepository projects, UserJpaRepository users) {
        this.repository = repository; this.projects = projects; this.users = users;
    }
    @Override @Transactional(readOnly = true)
    public List<IssueRecord> findByProject(UUID projectId, IssueStatus status, IssuePriority priority, String query) {
        return repository.findFiltered(projectId, status, priority, query).stream().map(JpaIssueStore::toRecord).toList();
    }
    @Override @Transactional(readOnly = true)
    public Optional<IssueRecord> findById(Long id) { return repository.findById(id).map(JpaIssueStore::toRecord); }
    @Override @Transactional
    public IssueRecord save(IssueRecord record) {
        IssueEntity entity = record.id() == null ? new IssueEntity() : repository.findById(record.id())
                .orElseThrow(() -> new NotFoundException("Дефект не найден"));
        ProjectEntity project = projects.findById(record.projectId())
                .orElseThrow(() -> new NotFoundException("Проект не найден"));
        UserEntity reporter = users.findByUsername(record.reporterUsername())
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        UserEntity assignee = record.assigneeUsername() == null ? null : users.findByUsername(record.assigneeUsername())
                .orElseThrow(() -> new NotFoundException("Исполнитель не найден"));
        entity.setProject(project); entity.setReporter(reporter); entity.setAssignee(assignee);
        entity.setTitle(record.title()); entity.setDescription(record.description());
        entity.setReproductionSteps(record.reproductionSteps()); entity.setExpectedResult(record.expectedResult());
        entity.setActualResult(record.actualResult()); entity.setGameVersion(record.gameVersion());
        entity.setPlatform(record.platform()); entity.setSeverity(record.severity()); entity.setPriority(record.priority());
        entity.setStatus(record.status()); entity.setCreatedAt(record.createdAt()); entity.setUpdatedAt(record.updatedAt());
        return toRecord(repository.saveAndFlush(entity));
    }
    static IssueRecord toRecord(IssueEntity entity) {
        return new IssueRecord(entity.getId(), entity.getProject().getId(), entity.getProject().getProjectKey(),
                entity.getTitle(), entity.getDescription(), entity.getReproductionSteps(), entity.getExpectedResult(),
                entity.getActualResult(), entity.getGameVersion(), entity.getPlatform(), entity.getSeverity(),
                entity.getPriority(), entity.getStatus(), entity.getReporter().getUsername(),
                entity.getAssignee() == null ? null : entity.getAssignee().getUsername(),
                entity.getCreatedAt(), entity.getUpdatedAt());
    }
}
