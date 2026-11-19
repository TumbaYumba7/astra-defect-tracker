package ru.mirea.coursework.defecttracker.adapter.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.mirea.coursework.defecttracker.adapter.persistence.entity.IssueEntity;
import ru.mirea.coursework.defecttracker.domain.IssuePriority;
import ru.mirea.coursework.defecttracker.domain.IssueStatus;
import java.util.List;
import java.util.UUID;

public interface IssueJpaRepository extends JpaRepository<IssueEntity, Long> {
    @Query("""
        select i from IssueEntity i
        where i.project.id = :projectId
          and (:status is null or i.status = :status)
          and (:priority is null or i.priority = :priority)
          and (:query is null or lower(i.title) like lower(concat('%', :query, '%'))
               or lower(i.description) like lower(concat('%', :query, '%')))
        order by i.updatedAt desc
        """)
    List<IssueEntity> findFiltered(@Param("projectId") UUID projectId,
                                   @Param("status") IssueStatus status,
                                   @Param("priority") IssuePriority priority,
                                   @Param("query") String query);
}
