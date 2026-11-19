package ru.mirea.coursework.defecttracker.adapter.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mirea.coursework.defecttracker.adapter.persistence.entity.HistoryEntity;
import java.util.List;

public interface HistoryJpaRepository extends JpaRepository<HistoryEntity, Long> {
    List<HistoryEntity> findByIssue_IdOrderByCreatedAtAsc(Long issueId);
}
