package com.teamtask.repository;

import com.teamtask.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    @Query("select distinct p from Project p left join p.members m where p.createdBy.id = :userId or m.id = :userId")
    List<Project> findVisibleToUser(@Param("userId") Long userId);
}
