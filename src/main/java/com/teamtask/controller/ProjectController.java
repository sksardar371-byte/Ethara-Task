package com.teamtask.controller;

import com.teamtask.dto.ProjectDtos.MemberResponse;
import com.teamtask.dto.ProjectDtos.ProjectRequest;
import com.teamtask.dto.ProjectDtos.ProjectResponse;
import com.teamtask.model.Project;
import com.teamtask.model.User;
import com.teamtask.repository.ProjectRepository;
import com.teamtask.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController extends BaseController {
    private final ProjectRepository projects;

    public ProjectController(UserRepository users, ProjectRepository projects) {
        super(users);
        this.projects = projects;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public List<ProjectResponse> list(Authentication auth) {
        User me = currentUser(auth);
        List<Project> visible = isAdmin(me) ? projects.findAll() : projects.findVisibleToUser(me.getId());
        return visible.stream().map(this::response).toList();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ProjectResponse create(@Valid @RequestBody ProjectRequest request, Authentication auth) {
        User me = currentUser(auth);
        Project project = new Project();
        project.setName(request.name().trim());
        project.setDescription(request.description());
        project.setCreatedBy(me);
        project.setMembers(resolveMembers(request.memberIds()));
        project.getMembers().add(me);
        return response(projects.save(project));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ProjectResponse update(@PathVariable Long id, @Valid @RequestBody ProjectRequest request) {
        Project project = projects.findById(id).orElseThrow(() -> new IllegalArgumentException("Project not found"));
        project.setName(request.name().trim());
        project.setDescription(request.description());
        project.setMembers(resolveMembers(request.memberIds()));
        project.getMembers().add(project.getCreatedBy());
        return response(projects.save(project));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        projects.deleteById(id);
    }

    public void assertProjectVisible(Project project, User user) {
        if (isAdmin(user) || project.getCreatedBy().getId().equals(user.getId()) || project.getMembers().stream().anyMatch(m -> m.getId().equals(user.getId()))) {
            return;
        }
        throw new AccessDeniedException("You do not have access to this project");
    }

    private HashSet<User> resolveMembers(List<Long> memberIds) {
        HashSet<User> members = new HashSet<>();
        if (memberIds != null) {
            memberIds.forEach(id -> members.add(users.findById(id).orElseThrow(() -> new IllegalArgumentException("Member not found: " + id))));
        }
        return members;
    }

    private ProjectResponse response(Project project) {
        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getDescription(),
                member(project.getCreatedBy()),
                project.getMembers().stream().map(this::member).toList(),
                project.getCreatedAt()
        );
    }

    private MemberResponse member(User user) {
        return new MemberResponse(user.getId(), user.getName(), user.getEmail(), user.getRole().name());
    }
}
