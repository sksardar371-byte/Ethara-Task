# Team Task Manager

Team Task Manager is a web app for managing projects, assigning tasks to team members, and tracking task progress. It has two roles:

- Admin: can create projects, add team members, assign tasks, and manage task data
- Member: can view assigned work and update task status

## Tech Stack

- Frontend: HTML, CSS, JavaScript
- Backend: Java Spring Boot
- Database: H2 for local development, PostgreSQL for Railway
- Auth: JWT-based login/signup
- ORM: Spring Data JPA / Hibernate

## Main Features

- User signup and login
- Role-based access for Admin and Member
- Project creation and team member selection
- Task creation with assignee, due date, and status
- Task status updates
- Dashboard summary for total, pending, in-progress, completed, and overdue tasks

## Default Admin Account

When the database is empty, the app creates one admin account:

```text
Email: admin@example.com
Password: admin123
```


## API Routes

- `POST /api/auth/signup`
- `POST /api/auth/login`
- `GET /api/users`
- `PATCH /api/users/{id}/role`
- `GET /api/projects`
- `POST /api/projects`
- `PUT /api/projects/{id}`
- `DELETE /api/projects/{id}`
- `GET /api/tasks`
- `POST /api/tasks`
- `PATCH /api/tasks/{id}/status`
- `DELETE /api/tasks/{id}`
- `GET /api/tasks/dashboard`

