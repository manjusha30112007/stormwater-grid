-- =======================================================================
-- MentorConnect - SQL Analytical Queries for MySQL Workbench
-- Test and verify all Core Features & Business Rules directly in SQL
-- =======================================================================

USE `mentorconnect`;

-- =======================================================================
-- QUERY 1: AUTO-SUGGEST BEST-MATCHING MENTORS BASED ON TAG OVERLAP
-- Business Rule 2: Suggestions ranked by number of overlapping tags, highest first.
-- Filters mentors who have available capacity.
-- Example for Student ID = 3 (Vikram Singh - Cloud / AWS / DevOps)
-- =======================================================================
SELECT 
    s.id AS student_id,
    s.name AS student_name,
    a.id AS mentor_id,
    a.name AS mentor_name,
    a.company,
    a.job_title,
    a.max_mentees,
    COALESCE(active_pairs.active_count, 0) AS current_active_mentees,
    (a.max_mentees - COALESCE(active_pairs.active_count, 0)) AS available_slots,
    COUNT(t_overlap.tag_id) AS tag_overlap_score,
    GROUP_CONCAT(DISTINCT t.name ORDER BY t.name SEPARATOR ', ') AS overlapping_tags
FROM students s
-- Join student desired tags
JOIN student_interest_tags sit ON s.id = sit.student_id
-- Cross with alumni
CROSS JOIN alumni a
-- Join alumni expertise tags matching student tag
LEFT JOIN alumni_expertise_tags t_overlap 
    ON a.id = t_overlap.alumni_id AND sit.tag_id = t_overlap.tag_id
LEFT JOIN interest_tags t ON t_overlap.tag_id = t.id
-- Count current active mentees for each mentor
LEFT JOIN (
    SELECT alumni_id, COUNT(*) AS active_count
    FROM mentorship_pairs
    WHERE status = 'ACTIVE'
    GROUP BY alumni_id
) active_pairs ON a.id = active_pairs.alumni_id
WHERE s.id = 3 -- Change student ID here to test different students
  AND (a.max_mentees - COALESCE(active_pairs.active_count, 0)) > 0 -- Must have available capacity
GROUP BY s.id, s.name, a.id, a.name, a.company, a.job_title, a.max_mentees, active_pairs.active_count
ORDER BY tag_overlap_score DESC, available_slots DESC, a.name ASC;


-- =======================================================================
-- QUERY 2: MENTOR CONCURRENT MENTEE CAPACITY ENFORCEMENT AUDIT
-- Business Rule 1: A mentor cannot be matched beyond their declared maximum concurrent mentee limit.
-- Displays which mentors have slots remaining vs who is at full capacity.
-- =======================================================================
SELECT 
    a.id AS mentor_id,
    a.name AS mentor_name,
    a.company,
    a.job_title,
    a.max_mentees AS declared_limit,
    COUNT(p.id) AS current_active_mentees,
    (a.max_mentees - COUNT(p.id)) AS remaining_slots,
    CASE 
        WHEN (a.max_mentees - COUNT(p.id)) <= 0 THEN 'FULL CAPACITY - MATCHING REJECTED'
        ELSE 'AVAILABLE'
    END AS capacity_status
FROM alumni a
LEFT JOIN mentorship_pairs p ON a.id = p.alumni_id AND p.status = 'ACTIVE'
GROUP BY a.id, a.name, a.company, a.job_title, a.max_mentees
ORDER BY remaining_slots ASC, a.name ASC;


-- =======================================================================
-- QUERY 3: CORE FEATURE 5 — MENTORSHIP ENGAGEMENT REPORT
-- Sessions held per mentor pair, status breakdown, and total hours delivered.
-- =======================================================================
SELECT 
    p.id AS pair_id,
    a.name AS mentor_name,
    a.company AS mentor_company,
    s.name AS mentee_name,
    s.department AS mentee_department,
    p.status AS pair_status,
    p.start_date,
    COUNT(sess.id) AS total_sessions,
    SUM(CASE WHEN sess.status = 'COMPLETED' THEN 1 ELSE 0 END) AS completed_sessions,
    SUM(CASE WHEN sess.status = 'SCHEDULED' THEN 1 ELSE 0 END) AS scheduled_sessions,
    SUM(CASE WHEN sess.status = 'CANCELLED' THEN 1 ELSE 0 END) AS cancelled_sessions,
    COALESCE(SUM(CASE WHEN sess.status = 'COMPLETED' THEN sess.duration_minutes ELSE 0 END), 0) AS total_completed_minutes,
    ROUND(COALESCE(SUM(CASE WHEN sess.status = 'COMPLETED' THEN sess.duration_minutes ELSE 0 END), 0) / 60.0, 1) AS total_completed_hours,
    MAX(sess.scheduled_time) AS latest_session_date
FROM mentorship_pairs p
JOIN alumni a ON p.alumni_id = a.id
JOIN students s ON p.student_id = s.id
LEFT JOIN mentoring_sessions sess ON p.id = sess.mentorship_pair_id
GROUP BY p.id, a.name, a.company, s.name, s.department, p.status, p.start_date
ORDER BY completed_sessions DESC, total_sessions DESC;


-- =======================================================================
-- QUERY 4: DETAILED SESSION ACTIVITY & COMPLETION STATUS LOG
-- Core Feature 4: Track scheduled mentoring sessions and completion status.
-- =======================================================================
SELECT 
    sess.id AS session_id,
    sess.title,
    sess.scheduled_time,
    sess.duration_minutes,
    sess.status,
    a.name AS mentor_name,
    s.name AS mentee_name,
    sess.feedback,
    sess.completed_at
FROM mentoring_sessions sess
JOIN mentorship_pairs p ON sess.mentorship_pair_id = p.id
JOIN alumni a ON p.alumni_id = a.id
JOIN students s ON p.student_id = s.id
ORDER BY sess.scheduled_time DESC;


-- =======================================================================
-- QUERY 5: TOP MENTORS BY DELIVERED MENTORING HOURS
-- =======================================================================
SELECT 
    a.id AS mentor_id,
    a.name AS mentor_name,
    a.company,
    COUNT(DISTINCT p.id) AS total_matched_mentees,
    COUNT(sess.id) AS completed_sessions_count,
    ROUND(COALESCE(SUM(sess.duration_minutes), 0) / 60.0, 1) AS total_mentoring_hours
FROM alumni a
LEFT JOIN mentorship_pairs p ON a.id = p.alumni_id
LEFT JOIN mentoring_sessions sess ON p.id = sess.mentorship_pair_id AND sess.status = 'COMPLETED'
GROUP BY a.id, a.name, a.company
ORDER BY total_mentoring_hours DESC, completed_sessions_count DESC;
