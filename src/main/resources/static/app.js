// ========================================================
// MentorConnect Web Dashboard Client Logic
// Communicates with Spring Boot REST API
// ========================================================

const API_BASE = '/api';

// State Cache
let state = {
    summary: {},
    alumni: [],
    students: [],
    pairs: [],
    sessions: [],
    report: [],
    selectedStudentId: null
};

// DOM Content Loaded
document.addEventListener('DOMContentLoaded', () => {
    initNavigation();
    loadAllData();
});

// Navigation Handling
function initNavigation() {
    const navItems = document.querySelectorAll('.nav-item');
    navItems.forEach(item => {
        item.addEventListener('click', () => {
            const targetTab = item.dataset.tab;
            switchTab(targetTab);
        });
    });
}

function switchTab(tabId) {
    document.querySelectorAll('.nav-item').forEach(b => b.classList.remove('active'));
    document.querySelectorAll('.tab-pane').forEach(p => p.classList.remove('active'));

    const btn = document.querySelector(`.nav-item[data-tab="${tabId}"]`);
    if (btn) btn.classList.add('active');

    const pane = document.getElementById(`tab-${tabId}`);
    if (pane) pane.classList.add('active');

    // Update Titles
    const titleMap = {
        'overview': { title: 'Platform Overview', sub: 'Real-time statistics & mentorship engagement overview' },
        'matching': { title: 'Auto-Suggest & Matching', sub: 'Algorithmically match students to alumni by tag overlap' },
        'alumni': { title: 'Alumni Mentors Directory', sub: 'Registered alumni with expertise and mentee capacity limits' },
        'students': { title: 'Student Mentees Directory', sub: 'Enrolled students and desired learning interest areas' },
        'pairs': { title: 'Mentorship Pairs', sub: 'Active and past mentor-mentee engagements' },
        'sessions': { title: 'Mentoring Sessions Tracker', sub: 'Scheduled, completed, and logged mentoring sessions' },
        'reports': { title: 'Mentorship Engagement Report', sub: 'Feature 5: Detailed report of sessions held per mentor pair' },
        'developer': { title: 'MySQL Workbench & Postman', sub: 'Testing guides, analytical SQL queries, and API collections' }
    };

    if (titleMap[tabId]) {
        document.getElementById('page-title').textContent = titleMap[tabId].title;
        document.getElementById('page-subtitle').textContent = titleMap[tabId].sub;
    }
}

// Data Fetching
async function loadAllData() {
    try {
        await Promise.all([
            fetchSummary(),
            fetchAlumni(),
            fetchStudents(),
            fetchPairs(),
            fetchSessions(),
            fetchEngagementReport()
        ]);
        populateStudentSelects();
        populateMentorSelects();
        populateActivePairSelect();
    } catch (err) {
        console.error('Error loading data:', err);
    }
}

// Fetch APIs
async function fetchSummary() {
    try {
        const res = await fetch(`${API_BASE}/reports/summary`);
        if (!res.ok) throw new Error('Failed to fetch summary');
        const data = await res.json();
        state.summary = data;

        document.getElementById('stat-alumni').textContent = data.totalAlumni || 0;
        document.getElementById('stat-students').textContent = data.totalStudents || 0;
        document.getElementById('stat-active-pairs').textContent = data.activePairs || 0;
        document.getElementById('stat-completed-sessions').textContent = data.completedSessions || 0;
        document.getElementById('stat-hours').textContent = (data.totalMentoringHours || 0) + ' hrs';
    } catch (e) {
        console.error(e);
    }
}

async function fetchAlumni() {
    try {
        const res = await fetch(`${API_BASE}/alumni`);
        if (!res.ok) throw new Error('Failed to fetch alumni');
        state.alumni = await res.json();
        renderAlumniTable();
    } catch (e) {
        console.error(e);
    }
}

async function fetchStudents() {
    try {
        const res = await fetch(`${API_BASE}/students`);
        if (!res.ok) throw new Error('Failed to fetch students');
        state.students = await res.json();
        renderStudentsTable();
    } catch (e) {
        console.error(e);
    }
}

async function fetchPairs() {
    try {
        const res = await fetch(`${API_BASE}/mentorship-pairs`);
        if (!res.ok) throw new Error('Failed to fetch pairs');
        state.pairs = await res.json();
        renderPairsTable();
    } catch (e) {
        console.error(e);
    }
}

async function fetchSessions() {
    try {
        const res = await fetch(`${API_BASE}/sessions`);
        if (!res.ok) throw new Error('Failed to fetch sessions');
        state.sessions = await res.json();
        renderSessionsTable();
    } catch (e) {
        console.error(e);
    }
}

async function fetchEngagementReport() {
    try {
        const res = await fetch(`${API_BASE}/reports/engagement`);
        if (!res.ok) throw new Error('Failed to fetch engagement report');
        state.report = await res.json();
        renderReportTable();
    } catch (e) {
        console.error(e);
    }
}

// Render Alumni Table
function renderAlumniTable() {
    const tbody = document.getElementById('alumni-tbody');
    if (!state.alumni.length) {
        tbody.innerHTML = '<tr><td colspan="7" class="text-center">No alumni mentors registered yet.</td></tr>';
        return;
    }

    tbody.innerHTML = state.alumni.map(a => {
        const tags = (a.expertiseTags || []).map(t => `<span class="tag-badge">${t.name}</span>`).join(' ');
        const slotsBadge = a.availableSlots > 0 
            ? `<span class="badge badge-active">${a.availableSlots} slot(s) open</span>`
            : `<span class="badge badge-cancelled">FULL (0 open)</span>`;

        return `
            <tr>
                <td>
                    <strong>${escapeHtml(a.name)}</strong><br>
                    <small style="color:var(--text-muted)">${escapeHtml(a.email)}</small>
                </td>
                <td>
                    ${escapeHtml(a.jobTitle || 'N/A')}<br>
                    <small style="color:var(--text-secondary)">@ ${escapeHtml(a.company || 'N/A')}</small>
                </td>
                <td><div class="tags-container">${tags || '<span class="helper-text">None</span>'}</div></td>
                <td><strong style="font-family:var(--font-mono)">${a.maxMentees}</strong></td>
                <td><strong style="font-family:var(--font-mono)">${a.activeMenteesCount}</strong></td>
                <td>${slotsBadge}</td>
                <td>
                    <button class="btn btn-outline btn-sm" onclick="matchWithMentorDirectly(${a.id})">Match</button>
                </td>
            </tr>
        `;
    }).join('');
}

// Render Students Table
function renderStudentsTable() {
    const tbody = document.getElementById('students-tbody');
    if (!state.students.length) {
        tbody.innerHTML = '<tr><td colspan="5" class="text-center">No students registered yet.</td></tr>';
        return;
    }

    tbody.innerHTML = state.students.map(s => {
        const tags = (s.interestTags || []).map(t => `<span class="tag-badge">${t.name}</span>`).join(' ');

        return `
            <tr>
                <td>
                    <strong>${escapeHtml(s.name)}</strong><br>
                    <small style="color:var(--text-muted)">${escapeHtml(s.email)}</small>
                </td>
                <td>${escapeHtml(s.department || 'General')}</td>
                <td><span style="font-family:var(--font-mono)">${s.batchYear || 'N/A'}</span></td>
                <td><div class="tags-container">${tags || '<span class="helper-text">No interest tags specified</span>'}</div></td>
                <td>
                    <button class="btn btn-primary btn-sm" onclick="suggestForStudentDirectly(${s.id})">Find Mentors 🎯</button>
                </td>
            </tr>
        `;
    }).join('');
}

// Render Mentorship Pairs Table
function renderPairsTable() {
    const tbody = document.getElementById('pairs-tbody');
    if (!state.pairs.length) {
        tbody.innerHTML = '<tr><td colspan="8" class="text-center">No mentorship pairs formed yet.</td></tr>';
        return;
    }

    tbody.innerHTML = state.pairs.map(p => {
        let badgeClass = 'badge-active';
        if (p.status === 'COMPLETED') badgeClass = 'badge-completed';
        if (p.status === 'CANCELLED') badgeClass = 'badge-cancelled';

        const startDate = p.startDate ? new Date(p.startDate).toLocaleDateString() : 'N/A';

        return `
            <tr>
                <td style="font-family:var(--font-mono)">#${p.id}</td>
                <td>
                    <strong>${escapeHtml(p.alumniName)}</strong><br>
                    <small style="color:var(--text-secondary)">${escapeHtml(p.alumniCompany || '')}</small>
                </td>
                <td>
                    <strong>${escapeHtml(p.studentName)}</strong><br>
                    <small style="color:var(--text-secondary)">${escapeHtml(p.studentDepartment || '')}</small>
                </td>
                <td><span class="badge ${badgeClass}">${p.status}</span></td>
                <td>${startDate}</td>
                <td><strong>${p.totalSessions}</strong></td>
                <td><span style="color:#34d399;font-weight:600">${p.completedSessions}</span></td>
                <td>
                    ${p.status === 'ACTIVE' ? `
                        <button class="btn btn-success btn-sm" onclick="completePairPrompt(${p.id})">Complete</button>
                        <button class="btn btn-danger btn-sm" onclick="cancelPairPrompt(${p.id})">Cancel</button>
                    ` : '<span class="helper-text">Concluded</span>'}
                </td>
            </tr>
        `;
    }).join('');
}

// Render Sessions Table
function renderSessionsTable() {
    const tbody = document.getElementById('sessions-tbody');
    if (!state.sessions.length) {
        tbody.innerHTML = '<tr><td colspan="8" class="text-center">No sessions recorded yet.</td></tr>';
        return;
    }

    tbody.innerHTML = state.sessions.map(sess => {
        let badgeClass = 'badge-scheduled';
        if (sess.status === 'COMPLETED') badgeClass = 'badge-active';
        if (sess.status === 'CANCELLED') badgeClass = 'badge-cancelled';

        const dt = sess.scheduledTime ? new Date(sess.scheduledTime).toLocaleString([], { dateStyle: 'short', timeStyle: 'short' }) : 'N/A';

        return `
            <tr>
                <td style="font-family:var(--font-mono)">#${sess.id}</td>
                <td>
                    <strong>${escapeHtml(sess.title)}</strong><br>
                    <small style="color:var(--text-muted)">${escapeHtml(sess.agenda || 'No agenda specified')}</small>
                </td>
                <td>
                    🎓 ${escapeHtml(sess.mentorName)}<br>
                    🎒 ${escapeHtml(sess.studentName)}
                </td>
                <td>${dt}</td>
                <td>${sess.durationMinutes} min</td>
                <td><span class="badge ${badgeClass}">${sess.status}</span></td>
                <td>
                    ${sess.feedback ? `<small style="color:#a5b4fc">"${escapeHtml(sess.feedback)}"</small>` : '<span class="helper-text">Pending completion</span>'}
                </td>
                <td>
                    ${sess.status === 'SCHEDULED' ? `
                        <button class="btn btn-success btn-sm" onclick="openCompleteSessionModal(${sess.id}, '${escapeHtml(sess.title)}')">Complete</button>
                        <button class="btn btn-outline btn-sm" onclick="cancelSessionPrompt(${sess.id})">Cancel</button>
                    ` : '<span class="helper-text">Done</span>'}
                </td>
            </tr>
        `;
    }).join('');
}

// Render Engagement Report Table (Feature 5)
function renderReportTable() {
    const tbody = document.getElementById('report-tbody');
    if (!state.report.length) {
        tbody.innerHTML = '<tr><td colspan="10" class="text-center">No engagement data available.</td></tr>';
        return;
    }

    tbody.innerHTML = state.report.map(r => {
        let statusBadge = r.pairStatus === 'ACTIVE' ? 'badge-active' : 'badge-completed';
        const lastDate = r.lastSessionDate ? new Date(r.lastSessionDate).toLocaleDateString() : 'N/A';

        return `
            <tr>
                <td style="font-family:var(--font-mono)">#${r.pairId}</td>
                <td>
                    <strong>${escapeHtml(r.mentorName)}</strong><br>
                    <small style="color:var(--text-secondary)">${escapeHtml(r.mentorCompany || '')}</small>
                </td>
                <td>
                    <strong>${escapeHtml(r.studentName)}</strong><br>
                    <small style="color:var(--text-secondary)">${escapeHtml(r.studentDepartment || '')}</small>
                </td>
                <td><span class="badge ${statusBadge}">${r.pairStatus}</span></td>
                <td><strong>${r.totalSessions}</strong></td>
                <td><span style="color:#34d399;font-weight:700">${r.completedSessions}</span></td>
                <td>${r.scheduledSessions}</td>
                <td>${r.cancelledSessions}</td>
                <td><strong style="color:#38bdf8">${r.totalCompletedHours} hrs</strong></td>
                <td><small>${lastDate}</small></td>
            </tr>
        `;
    }).join('');
}

// Populate Dropdown Selects
function populateStudentSelects() {
    const s1 = document.getElementById('overview-student-select');
    const s2 = document.getElementById('matching-student-select');
    const s3 = document.getElementById('pair-student-select');

    const options = state.students.map(s => `<option value="${s.id}">${escapeHtml(s.name)} (${escapeHtml(s.department || 'CS')})</option>`).join('');

    if (s1) s1.innerHTML = `<option value="">-- Choose Student --</option>` + options;
    if (s2) s2.innerHTML = `<option value="">-- Choose Student --</option>` + options;
    if (s3) s3.innerHTML = `<option value="">-- Choose Student --</option>` + options;
}

function populateMentorSelects() {
    const mSelect = document.getElementById('pair-mentor-select');
    if (!mSelect) return;

    mSelect.innerHTML = `<option value="">-- Choose Mentor --</option>` + 
        state.alumni.map(a => {
            const availText = a.availableSlots > 0 ? `(${a.availableSlots} slots open)` : `(FULL: 0 slots)`;
            return `<option value="${a.id}">${escapeHtml(a.name)} - ${escapeHtml(a.company || '')} ${availText}</option>`;
        }).join('');
}

function populateActivePairSelect() {
    const pSelect = document.getElementById('session-pair-select');
    if (!pSelect) return;

    const activePairs = state.pairs.filter(p => p.status === 'ACTIVE');
    pSelect.innerHTML = `<option value="">-- Choose Active Pair --</option>` + 
        activePairs.map(p => `<option value="${p.id}">Pair #${p.id}: ${escapeHtml(p.alumniName)} & ${escapeHtml(p.studentName)}</option>`).join('');
}

// Core Feature 3: Run Auto-Suggest Mentors
async function runAutoSuggest() {
    const select = document.getElementById('matching-student-select');
    const studentId = select.value;
    const previewContainer = document.getElementById('student-tags-badges');
    const suggestionsContainer = document.getElementById('suggestions-container');
    const suggestionsHeader = document.getElementById('suggestions-header');
    const countBadge = document.getElementById('suggestions-count');

    if (!studentId) {
        previewContainer.innerHTML = '<span class="tag-badge placeholder">Select a student above</span>';
        suggestionsContainer.innerHTML = `
            <div class="empty-state">
                <span>🔍</span>
                <p>Select a student above to see algorithmically ranked mentor suggestions.</p>
            </div>
        `;
        suggestionsHeader.classList.add('hidden');
        return;
    }

    const student = state.students.find(s => s.id == studentId);
    if (student) {
        const tags = (student.interestTags || []).map(t => `<span class="tag-badge overlapping">${t.name}</span>`).join(' ');
        previewContainer.innerHTML = tags || '<span class="helper-text">No interest tags specified for this student</span>';
    }

    suggestionsContainer.innerHTML = '<div class="empty-state"><span>⏳</span><p>Calculating tag overlap & available capacity...</p></div>';

    try {
        const res = await fetch(`${API_BASE}/matching/suggestions/${studentId}`);
        if (!res.ok) throw new Error('Failed to get match suggestions');
        const suggestions = await res.json();

        suggestionsHeader.classList.remove('hidden');
        countBadge.textContent = `${suggestions.length} available mentor(s) found`;

        if (!suggestions.length) {
            suggestionsContainer.innerHTML = `
                <div class="empty-state">
                    <span>⚠️</span>
                    <p>No available mentors found with capacity. Mentors may have reached their declared maximum mentee limits.</p>
                </div>
            `;
            return;
        }

        suggestionsContainer.innerHTML = suggestions.map((m, idx) => {
            const isTop = idx === 0 && m.overlapScore > 0;
            const overlapTagsHtml = (m.overlappingTags || []).map(t => `<span class="tag-badge overlapping">${t}</span>`).join(' ');
            const otherTags = Array.from(m.allExpertiseTags || []).filter(t => !m.overlappingTags.includes(t));
            const otherTagsHtml = otherTags.map(t => `<span class="tag-badge">${t}</span>`).join(' ');

            return `
                <div class="suggestion-card ${isTop ? 'top-match' : ''}">
                    <div class="suggestion-topbar">
                        <div class="mentor-meta">
                            <h4>${escapeHtml(m.name)}</h4>
                            <span class="mentor-role">${escapeHtml(m.jobTitle || '')} @ <strong>${escapeHtml(m.company || '')}</strong></span>
                        </div>
                        <div class="overlap-score-pill">
                            <span class="score-number">${m.overlapScore}</span>
                            <span class="score-txt">Tag Overlap</span>
                        </div>
                    </div>

                    <div class="slots-indicator">
                        <span>Max Limit: <strong>${m.maxMentees}</strong></span>
                        <span>Active: <strong>${m.activeMentees}</strong></span>
                        <span class="available">Available: <strong>${m.availableSlots}</strong></span>
                    </div>

                    <div class="tags-section">
                        <span class="tags-section-title">Matching Shared Tags:</span>
                        <div class="tags-container">
                            ${overlapTagsHtml || '<span class="helper-text">0 overlapping tags</span>'}
                        </div>
                    </div>

                    ${otherTagsHtml ? `
                        <div class="tags-section">
                            <span class="tags-section-title">Other Expertise:</span>
                            <div class="tags-container">
                                ${otherTagsHtml}
                            </div>
                        </div>
                    ` : ''}

                    <button class="btn btn-primary" style="margin-top:auto;" onclick="quickMatch(${m.alumniId}, ${studentId})">
                        Connect & Match Mentee 🤝
                    </button>
                </div>
            `;
        }).join('');

    } catch (err) {
        suggestionsContainer.innerHTML = `<div class="empty-state"><span>❌</span><p>Error calculating matches: ${err.message}</p></div>`;
    }
}

// Quick Match from Auto-Suggest
async function quickMatch(alumniId, studentId) {
    try {
        const res = await fetch(`${API_BASE}/mentorship-pairs`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                alumniId,
                studentId,
                notes: 'Matched via MentorConnect Auto-Suggest tag overlap algorithm'
            })
        });

        const data = await res.json();

        if (!res.ok) {
            // SHOW BUSINESS RULE REJECTION CLEARLY!
            showAlert(data.message || 'Matching rejected by business rules', 'error');
            return;
        }

        showAlert(`Match successfully established between ${data.alumniName} and ${data.studentName}!`, 'success');
        await loadAllData();
        runAutoSuggest();
    } catch (err) {
        showAlert('Network or server error establishing match: ' + err.message, 'error');
    }
}

// Overview Launch
function launchMatchingFromOverview() {
    const val = document.getElementById('overview-student-select').value;
    if (!val) {
        showAlert('Please select a student first.', 'error');
        return;
    }
    switchTab('matching');
    const sSelect = document.getElementById('matching-student-select');
    sSelect.value = val;
    runAutoSuggest();
}

function suggestForStudentDirectly(studentId) {
    switchTab('matching');
    const sSelect = document.getElementById('matching-student-select');
    sSelect.value = studentId;
    runAutoSuggest();
}

function matchWithMentorDirectly(alumniId) {
    openModal('modal-add-pair');
    document.getElementById('pair-mentor-select').value = alumniId;
}

// Form Handlers
async function handleRegisterAlumni(e) {
    e.preventDefault();
    const rawTags = document.getElementById('alumni-tags').value;
    const tagNames = rawTags.split(',').map(s => s.trim()).filter(s => s.length > 0);

    const payload = {
        name: document.getElementById('alumni-name').value,
        email: document.getElementById('alumni-email').value,
        company: document.getElementById('alumni-company').value,
        jobTitle: document.getElementById('alumni-title').value,
        graduationYear: parseInt(document.getElementById('alumni-year').value) || null,
        maxMentees: parseInt(document.getElementById('alumni-max-mentees').value) || 2,
        bio: document.getElementById('alumni-bio').value,
        tagNames
    };

    try {
        const res = await fetch(`${API_BASE}/alumni`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        const data = await res.json();
        if (!res.ok) {
            showAlert(data.message || 'Error registering alumni', 'error');
            return;
        }

        closeModal('modal-add-alumni');
        showAlert(`Alumni mentor ${data.name} registered successfully with ${data.maxMentees} declared slots!`, 'success');
        document.getElementById('form-add-alumni').reset();
        await loadAllData();
    } catch (err) {
        showAlert(err.message, 'error');
    }
}

async function handleRegisterStudent(e) {
    e.preventDefault();
    const rawTags = document.getElementById('student-tags').value;
    const interestTagNames = rawTags.split(',').map(s => s.trim()).filter(s => s.length > 0);

    const payload = {
        name: document.getElementById('student-name').value,
        email: document.getElementById('student-email').value,
        department: document.getElementById('student-dept').value,
        batchYear: parseInt(document.getElementById('student-batch').value) || null,
        interestTagNames
    };

    try {
        const res = await fetch(`${API_BASE}/students`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        const data = await res.json();
        if (!res.ok) {
            showAlert(data.message || 'Error registering student', 'error');
            return;
        }

        closeModal('modal-add-student');
        showAlert(`Student ${data.name} enrolled successfully!`, 'success');
        document.getElementById('form-add-student').reset();
        await loadAllData();
    } catch (err) {
        showAlert(err.message, 'error');
    }
}

async function handleCreatePair(e) {
    e.preventDefault();
    const payload = {
        alumniId: parseInt(document.getElementById('pair-mentor-select').value),
        studentId: parseInt(document.getElementById('pair-student-select').value),
        notes: document.getElementById('pair-notes').value
    };

    try {
        const res = await fetch(`${API_BASE}/mentorship-pairs`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        const data = await res.json();

        if (!res.ok) {
            // Highlights rule violation!
            showAlert(data.message || 'Match rejected by business rule', 'error');
            return;
        }

        closeModal('modal-add-pair');
        showAlert(`Mentorship match created for ${data.studentName} & ${data.alumniName}!`, 'success');
        document.getElementById('form-add-pair').reset();
        await loadAllData();
    } catch (err) {
        showAlert(err.message, 'error');
    }
}

// Session Modals
function openScheduleSessionModal() {
    populateActivePairSelect();
    const now = new Date();
    now.setDate(now.getDate() + 2);
    now.setHours(17, 0, 0, 0);
    // Format to yyyy-MM-ddTHH:mm
    const pad = n => String(n).padStart(2, '0');
    const defaultTime = `${now.getFullYear()}-${pad(now.getMonth()+1)}-${pad(now.getDate())}T${pad(now.getHours())}:${pad(now.getMinutes())}`;
    document.getElementById('session-time').value = defaultTime;
    openModal('modal-schedule-session');
}

async function handleScheduleSession(e) {
    e.preventDefault();
    const rawTime = document.getElementById('session-time').value; // yyyy-MM-ddTHH:mm
    const timeFormatted = rawTime.length === 16 ? rawTime + ':00' : rawTime;

    const payload = {
        mentorshipPairId: parseInt(document.getElementById('session-pair-select').value),
        title: document.getElementById('session-title').value,
        agenda: document.getElementById('session-agenda').value,
        scheduledTime: timeFormatted,
        durationMinutes: parseInt(document.getElementById('session-duration').value) || 60
    };

    try {
        const res = await fetch(`${API_BASE}/sessions`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        const data = await res.json();
        if (!res.ok) {
            showAlert(data.message || 'Error scheduling session', 'error');
            return;
        }

        closeModal('modal-schedule-session');
        showAlert(`Session "${data.title}" scheduled successfully!`, 'success');
        document.getElementById('form-schedule-session').reset();
        await loadAllData();
    } catch (err) {
        showAlert(err.message, 'error');
    }
}

function openCompleteSessionModal(sessionId, title) {
    document.getElementById('complete-session-id').value = sessionId;
    document.getElementById('complete-session-title').textContent = title;
    document.getElementById('complete-session-feedback').value = '';
    openModal('modal-complete-session');
}

async function handleCompleteSession(e) {
    e.preventDefault();
    const id = document.getElementById('complete-session-id').value;
    const feedback = document.getElementById('complete-session-feedback').value;

    try {
        const res = await fetch(`${API_BASE}/sessions/${id}/complete`, {
            method: 'PATCH',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ feedback })
        });
        const data = await res.json();
        if (!res.ok) {
            showAlert(data.message || 'Error completing session', 'error');
            return;
        }

        closeModal('modal-complete-session');
        showAlert(`Session #${id} completed and recorded in engagement report!`, 'success');
        await loadAllData();
    } catch (err) {
        showAlert(err.message, 'error');
    }
}

async function cancelSessionPrompt(sessionId) {
    const reason = prompt('Enter cancellation reason (optional):', 'Schedule conflict');
    if (reason === null) return;

    try {
        const res = await fetch(`${API_BASE}/sessions/${sessionId}/cancel`, {
            method: 'PATCH',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ reason })
        });
        const data = await res.json();
        if (!res.ok) {
            showAlert(data.message || 'Error cancelling session', 'error');
            return;
        }
        showAlert(`Session #${sessionId} cancelled.`, 'success');
        await loadAllData();
    } catch (err) {
        showAlert(err.message, 'error');
    }
}

async function completePairPrompt(pairId) {
    const notes = prompt('Enter conclusion notes for this mentorship engagement (optional):', 'Completed mentorship objectives successfully.');
    if (notes === null) return;

    try {
        const res = await fetch(`${API_BASE}/mentorship-pairs/${pairId}/complete`, {
            method: 'PATCH',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ notes })
        });
        const data = await res.json();
        if (!res.ok) {
            showAlert(data.message || 'Error concluding pair', 'error');
            return;
        }
        showAlert(`Mentorship Pair #${pairId} successfully concluded as COMPLETED!`, 'success');
        await loadAllData();
    } catch (err) {
        showAlert(err.message, 'error');
    }
}

async function cancelPairPrompt(pairId) {
    const notes = prompt('Enter reason for terminating mentorship:', 'Student changed interest track');
    if (notes === null) return;

    try {
        const res = await fetch(`${API_BASE}/mentorship-pairs/${pairId}/cancel`, {
            method: 'PATCH',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ notes })
        });
        const data = await res.json();
        if (!res.ok) {
            showAlert(data.message || 'Error cancelling pair', 'error');
            return;
        }
        showAlert(`Mentorship Pair #${pairId} cancelled.`, 'success');
        await loadAllData();
    } catch (err) {
        showAlert(err.message, 'error');
    }
}

// Modal Utilities
function openModal(id) {
    const m = document.getElementById(id);
    if (m) m.classList.add('active');
}

function closeModal(id) {
    const m = document.getElementById(id);
    if (m) m.classList.remove('active');
}

// Alert Banner Utility
function showAlert(message, type = 'info') {
    const banner = document.getElementById('alert-banner');
    const msgEl = document.getElementById('alert-message');
    const iconEl = document.getElementById('alert-icon');

    banner.className = `alert-banner ${type}`;
    msgEl.textContent = message;
    iconEl.textContent = type === 'error' ? '🚫' : (type === 'success' ? '✅' : 'ℹ️');
    banner.classList.remove('hidden');

    // Auto dismiss after 7 seconds
    setTimeout(() => {
        banner.classList.add('hidden');
    }, 7000);
}

function dismissAlert() {
    document.getElementById('alert-banner').classList.add('hidden');
}

function escapeHtml(str) {
    if (!str) return '';
    return str.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;").replace(/"/g, "&quot;").replace(/'/g, "&#039;");
}
