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

On a fresh database the app creates:

- Email: `admin@example.com`
- Password: `admin123`

Change this after deployment by creating real users and updating roles as needed.

## Run Locally

Install Java 17 and Maven, then run:

```bash
mvn spring-boot:run
```

Open:

```text
http://localhost:8080
```

Local data is stored in `./data/teamtask` using H2.

## Railway Deployment

1. Push this project to GitHub.
2. Create a new Railway project from the GitHub repo.
3. Add a PostgreSQL database service in Railway.
4. Set this environment variable on the app service:

```text
JWT_SECRET=replace-with-a-long-random-secret-at-least-32-characters
```

5. Railway will use Nixpacks to build with Maven and `railway.toml` to start the app on Railway's `$PORT`.
6. Open the generated Railway domain. The frontend and API are served from the same deployed app.

The app automatically reads Railway's `DATABASE_URL` and converts it to Spring Boot PostgreSQL settings.

## API Overview

- `POST /api/auth/signup`
- `POST /api/auth/login`
- `GET /api/users`
- `PATCH /api/users/{id}/role` Admin only
- `GET /api/projects`
- `POST /api/projects` Admin only
- `PUT /api/projects/{id}` Admin only
- `DELETE /api/projects/{id}` Admin only
- `GET /api/tasks`
- `POST /api/tasks` Admin only
- `PATCH /api/tasks/{id}/status`
- `DELETE /api/tasks/{id}` Admin only
- `GET /api/tasks/dashboard`

## Submission Checklist

- Live URL: add your Railway URL here
- GitHub repo: add your repository URL here
- README: included
- Demo video: record 2-5 minutes showing login, project creation, task assignment, member status update, and dashboard
