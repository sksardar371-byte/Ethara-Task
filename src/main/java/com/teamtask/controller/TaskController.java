package com.teamtask.controller;

import com.teamtask.dto.DashboardResponse;
import com.teamtask.dto.TaskDtos.StatusRequest;
import com.teamtask.dto.TaskDtos.TaskRequest;
import com.teamtask.dto.TaskDtos.TaskResponse;
import com.teamtask.model.Project;
import com.teamtask.model.Task;
import com.teamtask.model.TaskStatus;
import com.teamtask.model.User;
import com.teamtask.repository.ProjectRepository;
import com.teamtask.repository.TaskRepository;
import com.teamtask.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController extends BaseController {
    private final TaskRepository tasks;
    private final ProjectRepository projects;
    private final ProjectController projectController;

    public TaskController(UserRepository users, TaskRepository tasks, ProjectRepository projects, ProjectController projectController) {
        super(users);
        this.tasks = tasks;
        this.projects = projects;
        this.projectController = projectController;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public List<TaskResponse> list(Authentication auth) {
        User me = currentUser(auth);
        List<Task> visible = isAdmin(me) ? tasks.findAll() : tasks.findVisibleToUser(me.getId());
        return visible.stream().map(this::response).toList();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public TaskResponse create(@Valid @RequestBody TaskRequest request, Authentication auth) {
        Project project = projects.findById(request.projectId()).orElseThrow(() -> new IllegalArgumentException("Project not found"));
        User assignee = users.findById(request.assigneeId()).orElseThrow(() -> new IllegalArgumentException("Assignee not found"));
        if (project.getMembers().stream().noneMatch(m -> m.getId().equals(assignee.getId()))) {
            throw new IllegalArgumentException("Assignee must be a member of the project");
        }
        Task task = new Task();
        task.setTitle(request.title().trim());
        task.setDescription(request.description());
        task.setProject(project);
        task.setAssignee(assignee);
        task.setDueDate(request.dueDate());
        task.setCreatedBy(currentUser(auth));
        task.setStatus(request.status() == null ? TaskStatus.TODO : request.status());
        return response(tasks.save(task));
    }

    @PatchMapping("/{id}/status")
    @Transactional
    public TaskResponse updateStatus(@PathVariable Long id, @Valid @RequestBody StatusRequest request, Authentication auth) {
        User me = currentUser(auth);
        Task task = tasks.findById(id).orElseThrow(() -> new IllegalArgumentException("Task not found"));
        projectController.assertProjectVisible(task.getProject(), me);
        if (!isAdmin(me) && !task.getAssignee().getId().equals(me.getId())) {
            throw new AccessDeniedException("Only the assignee or an admin can update this task");
        }
        task.setStatus(request.status());
        return response(tasks.save(task));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        tasks.deleteById(id);
    }

    @GetMapping("/dashboard")
    @Transactional(readOnly = true)
    public DashboardResponse dashboard(Authentication auth) {
        User me = currentUser(auth);
        List<Task> visible = isAdmin(me) ? tasks.findAll() : tasks.findVisibleToUser(me.getId());
        long todo = visible.stream().filter(t -> t.getStatus() == TaskStatus.TODO).count();
        long inProgress = visible.stream().filter(t -> t.getStatus() == TaskStatus.IN_PROGRESS).count();
        long done = visible.stream().filter(t -> t.getStatus() == TaskStatus.DONE).count();
        long overdue = visible.stream().filter(t -> t.getDueDate().isBefore(LocalDate.now()) && t.getStatus() != TaskStatus.DONE).count();
        return new DashboardResponse(visible.size(), todo, inProgress, done, overdue);
    }

    private TaskResponse response(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getDueDate(),
                task.getProject().getId(),
                task.getProject().getName(),
                task.getAssignee().getId(),
                task.getAssignee().getName(),
                task.getCreatedBy().getId(),
                task.getCreatedAt(),
                task.getDueDate().isBefore(LocalDate.now()) && task.getStatus() != TaskStatus.DONE
        );
    }
}
