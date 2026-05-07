# Team Task Manager

A full-stack Team Task Manager assignment built with HTML, CSS, JavaScript, Java Spring Boot, Spring Security, JWT, JPA, and SQL database support.

## Features

- Signup and login with JWT authentication
- Admin and Member roles
- Admin project creation and team assignment
- Admin task assignment with due date and status
- Members can view their visible projects/tasks and update assigned task status
- Dashboard totals for total, to-do, in-progress, done, and overdue tasks
- SQL persistence with H2 locally and PostgreSQL on Railway

## Default Login

- Email: `admin@example.com`
- Password: `admin123`



## API Overview

- `POST /api/auth/signup`
- `POST /api/auth/login`
- `GET /api/users`
- `PATCH /api/users/{id}/role` 
- `GET /api/projects`
- `POST /api/projects` Admin only
- `PUT /api/projects/{id}` Admin only
- `DELETE /api/projects/{id}` Admin 
- `GET /api/tasks`
- `POST /api/tasks` Admin only
- `PATCH /api/tasks/{id}/status`
- `DELETE /api/tasks/{id}` Admin 
- `GET /api/tasks/dashboard`

