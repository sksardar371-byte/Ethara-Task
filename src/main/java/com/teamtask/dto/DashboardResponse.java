package com.teamtask.dto;

public record DashboardResponse(long totalTasks, long todo, long inProgress, long done, long overdue) {}
