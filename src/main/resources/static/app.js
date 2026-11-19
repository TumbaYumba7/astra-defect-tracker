const $ = (selector, root = document) => root.querySelector(selector);
const statusNames = {NEW:"Новый",TRIAGED:"Разобран",IN_PROGRESS:"В работе",READY_FOR_TEST:"На проверке",VERIFIED:"Проверен",CLOSED:"Закрыт",REOPENED:"Переоткрыт"};
const priorityNames = {LOW:"Низкий",NORMAL:"Обычный",HIGH:"Высокий",URGENT:"Срочный"};
const severityNames = {LOW:"Низкая",MEDIUM:"Средняя",HIGH:"Высокая",CRITICAL:"Критичная"};
const allowedTransitions = {NEW:["TRIAGED","CLOSED"],TRIAGED:["IN_PROGRESS","CLOSED"],IN_PROGRESS:["READY_FOR_TEST","TRIAGED"],READY_FOR_TEST:["VERIFIED","IN_PROGRESS"],VERIFIED:["CLOSED","REOPENED"],CLOSED:["REOPENED"],REOPENED:["TRIAGED","IN_PROGRESS"]};
let projects = [];
let issues = [];
let activeUser = null;
let selectedIssueId = null;

async function api(path, options = {}) {
  const headers = new Headers(options.headers || {});
  const authorization = sessionStorage.getItem("authorization");
  if (authorization) headers.set("Authorization", authorization);
  if (options.body && !(options.body instanceof FormData)) headers.set("Content-Type", "application/json");
  const response = await fetch(path, {...options, headers});
  const type = response.headers.get("content-type") || "";
  const payload = type.includes("json") ? await response.json() : null;
  if (!response.ok) throw new Error(payload?.detail || `Ошибка запроса (${response.status})`);
  return payload;
}

function setAuthMessage(message, isError = true) {
  const el = $("#authMessage");
  el.textContent = message;
  el.style.color = isError ? "var(--amber)" : "var(--accent)";
}

function showLogin() {
  $("#loginForm").classList.remove("hidden");
  $("#registerForm").classList.add("hidden");
  $("#showLogin").classList.add("active");
  $("#showRegister").classList.remove("active");
  setAuthMessage("");
}

function showRegister() {
  $("#registerForm").classList.remove("hidden");
  $("#loginForm").classList.add("hidden");
  $("#showRegister").classList.add("active");
  $("#showLogin").classList.remove("active");
  setAuthMessage("");
}

function showApplication(user) {
  activeUser = user;
  $("#loginView").classList.add("hidden");
  $("#appView").classList.remove("hidden");
  $("#userName").textContent = user.displayName;
  $("#userRole").textContent = user.role;
  $("#userAvatar").textContent = (user.displayName || user.username).slice(0, 1).toUpperCase();
  loadProjects();
}

function signOut() {
  sessionStorage.removeItem("authorization");
  activeUser = null;
  selectedIssueId = null;
  $("#appView").classList.add("hidden");
  $("#loginView").classList.remove("hidden");
  showLogin();
}

$("#showLogin").addEventListener("click", showLogin);
$("#showRegister").addEventListener("click", showRegister);
$("#logoutButton").addEventListener("click", signOut);

$("#loginForm").addEventListener("submit", async event => {
  event.preventDefault();
  const form = new FormData(event.currentTarget);
  const username = String(form.get("username")).trim();
  const password = String(form.get("password"));
  const encodedCredentials = new TextEncoder().encode(`${username}:${password}`);
  const authorization = `Basic ${btoa(String.fromCharCode(...encodedCredentials))}`;
  sessionStorage.setItem("authorization", authorization);
  try {
    const user = await api("/api/me");
    showApplication(user);
  } catch (error) {
    sessionStorage.removeItem("authorization");
    setAuthMessage(error.message || "Не удалось выполнить вход");
  }
});

$("#registerForm").addEventListener("submit", async event => {
  event.preventDefault();
  const data = Object.fromEntries(new FormData(event.currentTarget).entries());
  try {
    await api("/api/auth/register", {method:"POST", body:JSON.stringify(data)});
    showLogin();
    $("#loginForm [name=username]").value = data.username;
    setAuthMessage("Аккаунт создан. Войдите с выбранным паролем.", false);
  } catch (error) {
    setAuthMessage(error.message || "Не удалось создать аккаунт");
  }
});

async function loadProjects() {
  try {
    projects = await api("/api/projects");
    const select = $("#projectSelect");
    select.replaceChildren(...projects.map(project => {
      const option = document.createElement("option");
      option.value = project.id;
      option.textContent = `${project.projectKey} · ${project.name}`;
      return option;
    }));
    select.addEventListener("change", () => {
      const project = projects.find(item => item.id === select.value);
      $("#projectTitle").textContent = project?.name || "Проект";
    });
    const project = projects[0];
    $("#projectTitle").textContent = project?.name || "Проект";
    await loadIssues();
  } catch (error) {
    if (error.message.includes("401")) signOut();
    else $("#lastUpdated").textContent = error.message;
  }
}

$("#projectSelect").addEventListener("change", () => loadIssues());

$("#filterForm").addEventListener("submit", event => {
  event.preventDefault();
  loadIssues();
});

async function loadIssues() {
  const projectId = $("#projectSelect").value;
  if (!projectId) return;
  const params = new URLSearchParams();
  const data = new FormData($("#filterForm"));
  for (const key of ["q", "status", "priority"]) {
    const value = String(data.get(key) || "").trim();
    if (value) params.set(key, value);
  }
  try {
    issues = await api(`/api/projects/${projectId}/issues?${params.toString()}`);
    renderIssues();
    $("#lastUpdated").textContent = `Обновлено ${new Date().toLocaleTimeString("ru-RU", {hour:"2-digit", minute:"2-digit"})}`;
    $("#issueCount").textContent = String(issues.length);
    updateSummary();
    if (selectedIssueId && issues.some(issue => issue.id === selectedIssueId)) await selectIssue(selectedIssueId);
  } catch (error) {
    $("#lastUpdated").textContent = error.message;
  }
}

function updateSummary() {
  const open = issues.filter(issue => !["VERIFIED", "CLOSED"].includes(issue.status)).length;
  const critical = issues.filter(issue => ["HIGH", "CRITICAL"].includes(issue.severity)).length;
  const verify = issues.filter(issue => issue.status === "READY_FOR_TEST").length;
  $("#totalMetric").textContent = String(issues.length);
  $("#openMetric").textContent = String(open);
  $("#criticalMetric").textContent = String(critical);
  $("#verifyMetric").textContent = String(verify);
}

function renderIssues() {
  const tbody = $("#issueRows");
  tbody.replaceChildren();
  $("#emptyState").classList.toggle("hidden", issues.length > 0);
  for (const issue of issues) {
    const row = document.createElement("tr");
    if (issue.id === selectedIssueId) row.classList.add("selected");
    row.addEventListener("click", () => selectIssue(issue.id));
    const defect = document.createElement("td");
    const title = document.createElement("div");
    title.className = "issue-title";
    const marker = document.createElement("span");
    marker.className = `issue-mark${issue.severity === "CRITICAL" ? " critical" : ""}`;
    const titleCopy = document.createElement("div");
    const titleStrong = document.createElement("strong");
    titleStrong.textContent = issue.title;
    const key = document.createElement("small");
    key.textContent = issue.issueKey;
    titleCopy.append(titleStrong, key);
    title.append(marker, titleCopy);
    defect.append(title);
    const state = document.createElement("td");
    const stateLabel = document.createElement("span");
    stateLabel.className = `state-label state-${issue.status}`;
    stateLabel.textContent = statusNames[issue.status] || issue.status;
    state.append(stateLabel);
    const priority = document.createElement("td");
    const priorityLabel = document.createElement("span");
    priorityLabel.className = `priority-label priority-${issue.priority}`;
    priorityLabel.textContent = priorityNames[issue.priority] || issue.priority;
    priority.append(priorityLabel);
    const build = document.createElement("td");
    build.textContent = issue.gameVersion;
    const assignee = document.createElement("td");
    assignee.textContent = issue.assigneeUsername || "Не назначен";
    row.append(defect, state, priority, build, assignee);
    tbody.append(row);
  }
}

function addField(container, label, value) {
  const item = document.createElement("div");
  item.className = "detail-field";
  const small = document.createElement("small");
  small.textContent = label;
  const strong = document.createElement("strong");
  strong.textContent = value || "—";
  item.append(small, strong);
  container.append(item);
}

async function selectIssue(id) {
  selectedIssueId = id;
  renderIssues();
  const issue = await api(`/api/issues/${id}`);
  const [comments, history] = await Promise.all([
    api(`/api/issues/${id}/comments`),
    api(`/api/issues/${id}/history`)
  ]);
  const panel = $("#detailPanel");
  panel.replaceChildren();
  const header = document.createElement("header");
  header.className = "detail-header";
  const kicker = document.createElement("div");
  kicker.className = "detail-kicker";
  kicker.textContent = issue.issueKey;
  const status = document.createElement("span");
  status.className = `state-label state-${issue.status}`;
  status.textContent = statusNames[issue.status];
  kicker.append(status);
  const title = document.createElement("h2");
  title.textContent = issue.title;
  const chips = document.createElement("div");
  chips.className = "detail-meta";
  for (const label of [`${severityNames[issue.severity]} серьёзность`, `${priorityNames[issue.priority]} приоритет`, issue.platform]) {
    const chip = document.createElement("span");
    chip.className = "chip";
    chip.textContent = label;
    chips.append(chip);
  }
  header.append(kicker, title, chips);
  panel.append(header);

  const addBlock = (label, text) => {
    const block = document.createElement("section");
    block.className = "detail-block";
    const heading = document.createElement("h3");
    heading.textContent = label;
    const copy = document.createElement("p");
    copy.textContent = text || "—";
    block.append(heading, copy);
    panel.append(block);
  };
  addBlock("ОПИСАНИЕ", issue.description);
  addBlock("ШАГИ ВОСПРОИЗВЕДЕНИЯ", issue.reproductionSteps);
  const expectedActual = document.createElement("section");
  expectedActual.className = "detail-block detail-fields";
  addField(expectedActual, "Ожидается", issue.expectedResult);
  addField(expectedActual, "Получено", issue.actualResult);
  addField(expectedActual, "Версия", issue.gameVersion);
  addField(expectedActual, "Заявитель", issue.reporterUsername);
  addField(expectedActual, "Ответственный", issue.assigneeUsername || "Не назначен");
  panel.append(expectedActual);

  if (["ADMIN", "DEVELOPER", "TESTER"].includes(activeUser.role)) {
    const block = document.createElement("section");
    block.className = "detail-block";
    const heading = document.createElement("h3");
    heading.textContent = "ИЗМЕНИТЬ СОСТОЯНИЕ";
    const control = document.createElement("form");
    control.className = "status-control";
    const select = document.createElement("select");
    select.name = "status";
    for (const next of allowedTransitions[issue.status] || []) {
      const option = document.createElement("option"); option.value = next; option.textContent = statusNames[next]; select.append(option);
    }
    const button = document.createElement("button"); button.className = "button subtle"; button.textContent = "Сохранить";
    control.append(select, button);
    control.addEventListener("submit", async event => {
      event.preventDefault();
      try {
        await api(`/api/issues/${id}/status`, {method:"PATCH", body:JSON.stringify({status:select.value})});
        await loadIssues();
      } catch (error) { alert(error.message); }
    });
    block.append(heading, control);
    panel.append(block);
  }

  const commentsBlock = document.createElement("section");
  commentsBlock.className = "detail-block";
  const commentsHeading = document.createElement("h3");
  commentsHeading.textContent = `ОБСУЖДЕНИЕ · ${comments.length}`;
  const commentList = document.createElement("div");
  commentList.className = "comment-list";
  for (const comment of comments) {
    const item = document.createElement("article"); item.className = "comment-item";
    const author = document.createElement("strong"); author.textContent = comment.authorUsername;
    const time = document.createElement("time"); time.textContent = new Date(comment.createdAt).toLocaleDateString("ru-RU");
    const body = document.createElement("p"); body.textContent = comment.body;
    item.append(author, time, body); commentList.append(item);
  }
  const commentForm = document.createElement("form"); commentForm.className = "comment-form";
  const commentInput = document.createElement("input"); commentInput.name = "body"; commentInput.placeholder = "Добавить комментарий"; commentInput.maxLength = 5000; commentInput.required = true;
  const commentButton = document.createElement("button"); commentButton.className = "button primary"; commentButton.textContent = "Отправить";
  commentForm.append(commentInput, commentButton);
  commentForm.addEventListener("submit", async event => {
    event.preventDefault();
    try { await api(`/api/issues/${id}/comments`, {method:"POST", body:JSON.stringify({body:commentInput.value})}); await selectIssue(id); }
    catch (error) { alert(error.message); }
  });
  commentsBlock.append(commentsHeading, commentList, commentForm);
  panel.append(commentsBlock);

  const historyBlock = document.createElement("section"); historyBlock.className = "detail-block";
  const historyHeading = document.createElement("h3"); historyHeading.textContent = "ИСТОРИЯ СОСТОЯНИЙ";
  const historyList = document.createElement("div"); historyList.className = "history-list";
  for (const entry of history) {
    const item = document.createElement("div"); item.className = "history-item";
    item.textContent = `${entry.oldStatus ? statusNames[entry.oldStatus] : "Создан"} → ${statusNames[entry.newStatus]}`;
    const meta = document.createElement("small"); meta.textContent = `${entry.actorUsername} · ${new Date(entry.createdAt).toLocaleString("ru-RU")}`;
    item.append(meta); historyList.append(item);
  }
  if (!history.length) { const empty = document.createElement("p"); empty.className = "muted"; empty.textContent = "История пока пуста"; historyList.append(empty); }
  historyBlock.append(historyHeading, historyList); panel.append(historyBlock);
}

const issueDialog = $("#issueDialog");
$("#newIssueButton").addEventListener("click", () => issueDialog.showModal());
document.querySelectorAll(".close-dialog").forEach(button => button.addEventListener("click", () => issueDialog.close()));
$("#issueForm").addEventListener("submit", async event => {
  event.preventDefault();
  const projectId = $("#projectSelect").value;
  const request = Object.fromEntries(new FormData(event.currentTarget).entries());
  try {
    const created = await api(`/api/projects/${projectId}/issues`, {method:"POST", body:JSON.stringify(request)});
    issueDialog.close();
    event.currentTarget.reset();
    await loadIssues();
    await selectIssue(created.id);
  } catch (error) {
    $("#issueFormError").textContent = error.message;
  }
});

const existingAuthorization = sessionStorage.getItem("authorization");
if (existingAuthorization) {
  api("/api/me").then(showApplication).catch(() => signOut());
}
