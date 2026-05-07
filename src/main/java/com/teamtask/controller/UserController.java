package com.teamtask.controller;

import com.teamtask.dto.ProjectDtos.MemberResponse;
import com.teamtask.model.Role;
import com.teamtask.model.User;
import com.teamtask.repository.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController extends BaseController {
    public UserController(UserRepository users) {
        super(users);
    }

    @GetMapping
    public List<MemberResponse> allUsers() {
        return users.findAll().stream().map(this::member).toList();
    }

    @PatchMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public MemberResponse updateRole(@PathVariable Long id, @RequestBody Map<String, String> body) {
        User user = users.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setRole(Role.valueOf(body.getOrDefault("role", "MEMBER")));
        return member(users.save(user));
    }

    private MemberResponse member(User user) {
        return new MemberResponse(user.getId(), user.getName(), user.getEmail(), user.getRole().name());
    }
}
