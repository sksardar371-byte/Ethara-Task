const state = {
    token: localStorage.getItem("token"),
    user: JSON.parse(localStorage.getItem("user") || "null"),
    users: [],
    projects: [],
    tasks: [],
    dashboard: null
};

const $ = (id) => document.getElementById(id);

function setAuth(user, token) {
    state.user = user;
    state.token = token;
    localStorage.setItem("user", JSON.stringify(user));
    localStorage.setItem("token", token);
}

function clearAuth() {
    state.user = null;
    state.token = null;
    localStorage.removeItem("user");
    localStorage.removeItem("token");
}

async function api(path, options = {}) {
    const headers = { "Content-Type": "application/json", ...(options.headers || {}) };
    if (state.token) headers.Authorization = `Bearer ${state.token}`;
    const res = await fetch(path, { ...options, headers });
    if (!res.ok) {
        const error = await res.json().catch(() => ({ message: "Request failed" }));
        throw new Error(error.message || "Request failed");
    }
    if (res.status === 204) return null;
    return res.json();
}

function authPayload(data) {
    return { id: data.id, name: data.name, email: data.email, role: data.role };
}

function showMessage(id, text) {
    $(id).textContent = text || "";
}

function showApp() {
    $("authView").classList.add("hidden");
    $("appView").classList.remove("hidden");
    $("currentUser").textContent = `${state.user.name} (${state.user.role})`;
    $("adminTools").classList.toggle("hidden", state.user.role !== "ADMIN");
}

function showAuth() {
    $("authView").classList.remove("hidden");
    $("appView").classList.add("hidden");
}

async function loadAll() {
    [state.users, state.projects, state.tasks, state.dashboard] = await Promise.all([
        api("/api/users"),
        api("/api/projects"),
        api("/api/tasks"),
        api("/api/tasks/dashboard")
    ]);
    render();
}

function render() {
    showApp();
    renderMetrics();
    renderFormOptions();
    renderTasks();
    renderProjects();
}

function renderMetrics() {
    const d = state.dashboard || { totalTasks: 0, todo: 0, inProgress: 0, done: 0, overdue: 0 };
    $("metrics").innerHTML = [
        ["Total tasks", d.totalTasks],
        ["To do", d.todo],
        ["In progress", d.inProgress],
        ["Done", d.done],
        ["Overdue", d.overdue]
    ].map(([label, value]) => `<article class="metric"><span>${label}</span><strong>${value}</strong></article>`).join("");
}

function renderFormOptions() {
    const userOptions = state.users.map(u => `<option value="${u.id}">${escapeHtml(u.name)} - ${u.role}</option>`).join("");
    $("projectMembers").innerHTML = userOptions;
    $("taskProject").innerHTML = state.projects.map(p => `<option value="${p.id}">${escapeHtml(p.name)}</option>`).join("");
    renderTaskAssignees();
}

function renderTaskAssignees() {
    const project = state.projects.find(p => p.id === Number($("taskProject").value));
    const members = project ? project.members : state.users;
    $("taskAssignee").innerHTML = members.map(u => `<option value="${u.id}">${escapeHtml(u.name)} - ${u.role}</option>`).join("");
}

function renderTasks() {
    const filter = $("statusFilter").value;
    let tasks = [...state.tasks].sort((a, b) => a.dueDate.localeCompare(b.dueDate));
    if (filter === "OVERDUE") tasks = tasks.filter(t => t.overdue);
    if (filter !== "ALL" && filter !== "OVERDUE") tasks = tasks.filter(t => t.status === filter);

    $("taskList").innerHTML = tasks.length ? tasks.map(taskCard).join("") : `<p class="meta">No tasks match this view.</p>`;
}

function taskCard(task) {
    const statusClass = task.overdue ? "overdue" : task.status === "DONE" ? "done" : task.status === "IN_PROGRESS" ? "progress" : "";
    return `
        <article class="task-card">
            <div class="task-top">
                <div>
                    <h3>${escapeHtml(task.title)}</h3>
                    <p class="meta">${escapeHtml(task.projectName)} · assigned to ${escapeHtml(task.assigneeName)}</p>
                    <p class="meta">Due ${task.dueDate}${task.description ? ` · ${escapeHtml(task.description)}` : ""}</p>
                </div>
                <span class="badge ${statusClass}">${labelStatus(task.overdue ? "OVERDUE" : task.status)}</span>
            </div>
            ${canUpdate(task) ? `
                <div class="task-actions">
                    ${["TODO", "IN_PROGRESS", "DONE"].map(status => `
                        <button class="${task.status === status ? "active" : ""}" data-task="${task.id}" data-status="${status}" type="button">${labelStatus(status)}</button>
                    `).join("")}
                </div>
            ` : ""}
        </article>
    `;
}

function renderProjects() {
    $("projectList").innerHTML = state.projects.length ? state.projects.map(project => `
        <article class="project-card">
            <h3>${escapeHtml(project.name)}</h3>
            <p class="meta">${escapeHtml(project.description || "No description")}</p>
            <p class="meta">Owner: ${escapeHtml(project.createdBy.name)}</p>
            <div class="members">
                ${project.members.map(m => `<span class="badge">${escapeHtml(m.name)}</span>`).join("")}
            </div>
        </article>
    `).join("") : `<p class="meta">No projects yet.</p>`;
}

function canUpdate(task) {
    return state.user.role === "ADMIN" || task.assigneeId === state.user.id;
}

function labelStatus(status) {
    return {
        TODO: "To do",
        IN_PROGRESS: "In progress",
        DONE: "Done",
        OVERDUE: "Overdue"
    }[status] || status;
}

function selectedValues(select) {
    return Array.from(select.selectedOptions).map(option => Number(option.value));
}

function escapeHtml(value) {
    return String(value).replace(/[&<>"']/g, char => ({
        "&": "&amp;",
        "<": "&lt;",
        ">": "&gt;",
        '"': "&quot;",
        "'": "&#039;"
    }[char]));
}

$("loginTab").addEventListener("click", () => {
    $("loginTab").classList.add("active");
    $("signupTab").classList.remove("active");
    $("loginForm").classList.remove("hidden");
    $("signupForm").classList.add("hidden");
});

$("signupTab").addEventListener("click", () => {
    $("signupTab").classList.add("active");
    $("loginTab").classList.remove("active");
    $("signupForm").classList.remove("hidden");
    $("loginForm").classList.add("hidden");
});

$("loginForm").addEventListener("submit", async (event) => {
    event.preventDefault();
    try {
        const data = await api("/api/auth/login", {
            method: "POST",
            body: JSON.stringify({ email: $("loginEmail").value, password: $("loginPassword").value })
        });
        setAuth(authPayload(data), data.token);
        await loadAll();
    } catch (error) {
        showMessage("authMessage", error.message);
    }
});

$("signupForm").addEventListener("submit", async (event) => {
    event.preventDefault();
    try {
        const data = await api("/api/auth/signup", {
            method: "POST",
            body: JSON.stringify({ name: $("signupName").value, email: $("signupEmail").value, password: $("signupPassword").value })
        });
        setAuth(authPayload(data), data.token);
        await loadAll();
    } catch (error) {
        showMessage("authMessage", error.message);
    }
});

$("logoutBtn").addEventListener("click", () => {
    clearAuth();
    showAuth();
});

$("projectForm").addEventListener("submit", async (event) => {
    event.preventDefault();
    try {
        await api("/api/projects", {
            method: "POST",
            body: JSON.stringify({
                name: $("projectName").value,
                description: $("projectDescription").value,
                memberIds: selectedValues($("projectMembers"))
            })
        });
        event.target.reset();
        await loadAll();
    } catch (error) {
        showMessage("appMessage", error.message);
    }
});

$("taskForm").addEventListener("submit", async (event) => {
    event.preventDefault();
    try {
        await api("/api/tasks", {
            method: "POST",
            body: JSON.stringify({
                title: $("taskTitle").value,
                description: $("taskDescription").value,
                projectId: Number($("taskProject").value),
                assigneeId: Number($("taskAssignee").value),
                dueDate: $("taskDueDate").value,
                status: $("taskStatus").value
            })
        });
        event.target.reset();
        await loadAll();
    } catch (error) {
        showMessage("appMessage", error.message);
    }
});

$("taskList").addEventListener("click", async (event) => {
    const button = event.target.closest("button[data-task]");
    if (!button) return;
    try {
        await api(`/api/tasks/${button.dataset.task}/status`, {
            method: "PATCH",
            body: JSON.stringify({ status: button.dataset.status })
        });
        await loadAll();
    } catch (error) {
        showMessage("appMessage", error.message);
    }
});

$("statusFilter").addEventListener("change", renderTasks);
$("taskProject").addEventListener("change", renderTaskAssignees);

if (state.token && state.user) {
    loadAll().catch(() => {
        clearAuth();
        showAuth();
    });
} else {
    showAuth();
}
