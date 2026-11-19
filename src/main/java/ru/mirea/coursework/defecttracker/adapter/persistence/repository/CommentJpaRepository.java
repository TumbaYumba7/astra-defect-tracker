package ru.mirea.coursework.defecttracker.adapter.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mirea.coursework.defecttracker.adapter.persistence.entity.CommentEntity;
import java.util.List;

public interface CommentJpaRepository extends JpaRepository<CommentEntity, Long> {
    List<CommentEntity> findByIssue_IdOrderByCreatedAtAsc(Long issueId);
}
