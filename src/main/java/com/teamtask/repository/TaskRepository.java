package com.teamtask.repository;

import com.teamtask.model.Task;
import com.teamtask.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByProjectId(Long projectId);
    List<Task> findByAssigneeId(Long assigneeId);
    long countByStatus(TaskStatus status);
    long countByDueDateBeforeAndStatusNot(LocalDate date, TaskStatus status);

    @Query("select distinct t from Task t left join t.project.members m where t.project.createdBy.id = :userId or t.assignee.id = :userId or m.id = :userId")
    List<Task> findVisibleToUser(@Param("userId") Long userId);
}
