package com.teamtask.dto;

import com.teamtask.model.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.time.LocalDate;

public class TaskDtos {
    public record TaskRequest(
            @NotBlank String title,
            String description,
            @NotNull Long projectId,
            @NotNull Long assigneeId,
            @NotNull LocalDate dueDate,
            TaskStatus status
    ) {}

    public record StatusRequest(@NotNull TaskStatus status) {}

    public record TaskResponse(
            Long id,
            String title,
            String description,
            TaskStatus status,
            LocalDate dueDate,
            Long projectId,
            String projectName,
            Long assigneeId,
            String assigneeName,
            Long createdById,
            Instant createdAt,
            boolean overdue
    ) {}
}
