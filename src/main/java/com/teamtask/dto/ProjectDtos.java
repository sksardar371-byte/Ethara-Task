package com.teamtask.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.Instant;
import java.util.List;

public class ProjectDtos {
    public record ProjectRequest(@NotBlank String name, String description, List<Long> memberIds) {}
    public record MemberResponse(Long id, String name, String email, String role) {}
    public record ProjectResponse(Long id, String name, String description, MemberResponse createdBy, List<MemberResponse> members, Instant createdAt) {}
}
