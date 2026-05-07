package com.teamtask.controller;

import com.teamtask.model.Role;
import com.teamtask.model.User;
import com.teamtask.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;

abstract class BaseController {
    protected final UserRepository users;

    protected BaseController(UserRepository users) {
        this.users = users;
    }

    protected User currentUser(Authentication auth) {
        return users.findByEmail(auth.getName()).orElseThrow(() -> new AccessDeniedException("User not found"));
    }

    protected boolean isAdmin(User user) {
        return user.getRole() == Role.ADMIN;
    }
}
