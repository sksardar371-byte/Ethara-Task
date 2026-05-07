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

## Run Locally

Requirements:

- Java 17
- Maven

Start the project:

```bash
mvn spring-boot:run
```

Open the app:

```text
http://localhost:8080
```

Local data is saved in the `data` folder using H2.

## Railway Deployment

1. Push the project to GitHub.
2. Create a new Railway project.
3. Select this GitHub repository.
4. Add a PostgreSQL database service in Railway.
5. Add this variable in the app service:

```text
JWT_SECRET=your-long-secret-key-here
```

Railway will build the app using Nixpacks and start it with the command from `railway.toml`.

After deployment, generate a public domain from Railway's Networking settings.

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

## Submission Links

- Live URL:
- GitHub Repo: https://github.com/sksardar371-byte/Ethara-Task
- Demo Video:
