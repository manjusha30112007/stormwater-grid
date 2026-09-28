-- ========================================================
-- MentorConnect - Alumni-Student Mentorship Matching Platform
-- Sample Data (DML) for MySQL Workbench
-- ========================================================

USE `mentorconnect`;

-- 1. Insert Interest Tags
INSERT INTO `interest_tags` (`id`, `name`) VALUES
(1, 'Java'),
(2, 'Spring Boot'),
(3, 'Cloud Computing'),
(4, 'AWS'),
(5, 'Machine Learning'),
(6, 'Artificial Intelligence'),
(7, 'Microservices'),
(8, 'System Design'),
(9, 'DevOps'),
(10, 'Data Science'),
(11, 'React'),
(12, 'Cybersecurity')
ON DUPLICATE KEY UPDATE `name`=VALUES(`name`);

-- 2. Insert Alumni Mentors
-- Notice: Arjun Sharma has max_mentees = 2
-- Priya Patel has max_mentees = 1 (Exclusive 1-on-1 mentorship)
-- Rohit Verma has max_mentees = 2
-- Sneha Rao has max_mentees = 1
INSERT INTO `alumni` (`id`, `name`, `email`, `company`, `job_title`, `graduation_year`, `bio`, `max_mentees`, `created_at`) VALUES
(1, 'Arjun Sharma', 'arjun.sharma@google.com', 'Google', 'Senior Software Engineer', 2019, 'Passionate about distributed backend systems, Spring Boot microservices, and high-scale architectures.', 2, NOW()),
(2, 'Priya Patel', 'priya.patel@microsoft.com', 'Microsoft', 'Lead AI Scientist', 2018, 'Specializing in Deep Learning, NLP, and LLM fine-tuning pipelines. High demand mentor with 1 mentee max slot.', 1, NOW()),
(3, 'Rohit Verma', 'rohit.verma@amazon.com', 'Amazon', 'Principal Cloud Architect', 2017, 'AWS Solutions Architect with 8+ years designing fault-tolerant cloud infrastructures and CI/CD pipelines.', 2, NOW()),
(4, 'Sneha Rao', 'sneha.rao@stripe.com', 'Stripe', 'Staff Fullstack Engineer', 2020, 'Building high-reliability payment workflows using Java backends and modern React frontends.', 1, NOW())
ON DUPLICATE KEY UPDATE `name`=VALUES(`name`);

-- 3. Associate Alumni with Expertise Tags
INSERT IGNORE INTO `alumni_expertise_tags` (`alumni_id`, `tag_id`) VALUES
(1, 1), -- Arjun: Java
(1, 2), -- Arjun: Spring Boot
(1, 7), -- Arjun: Microservices
(1, 8), -- Arjun: System Design
(1, 3), -- Arjun: Cloud Computing
(2, 5), -- Priya: Machine Learning
(2, 6), -- Priya: Artificial Intelligence
(2, 10), -- Priya: Data Science
(3, 3), -- Rohit: Cloud Computing
(3, 4), -- Rohit: AWS
(3, 9), -- Rohit: DevOps
(3, 7), -- Rohit: Microservices
(4, 1), -- Sneha: Java
(4, 2), -- Sneha: Spring Boot
(4, 8), -- Sneha: System Design
(4, 11); -- Sneha: React

-- 4. Insert Students
INSERT INTO `students` (`id`, `name`, `email`, `department`, `batch_year`, `created_at`) VALUES
(1, 'Rahul Kumar', 'rahul.kumar@college.edu', 'Computer Science', 2026, NOW()),
(2, 'Ananya Iyer', 'ananya.iyer@college.edu', 'Artificial Intelligence', 2026, NOW()),
(3, 'Vikram Singh', 'vikram.singh@college.edu', 'Information Technology', 2027, NOW()),
(4, 'Deepa Menon', 'deepa.menon@college.edu', 'Computer Science', 2027, NOW())
ON DUPLICATE KEY UPDATE `name`=VALUES(`name`);

-- 5. Associate Students with Desired Interest Tags
INSERT IGNORE INTO `student_interest_tags` (`student_id`, `tag_id`) VALUES
(1, 1), -- Rahul: Java
(1, 2), -- Rahul: Spring Boot
(1, 8), -- Rahul: System Design
(2, 5), -- Ananya: Machine Learning
(2, 6), -- Ananya: Artificial Intelligence
(2, 10), -- Ananya: Data Science
(3, 3), -- Vikram: Cloud Computing
(3, 4), -- Vikram: AWS
(3, 9), -- Vikram: DevOps
(4, 1), -- Deepa: Java
(4, 2), -- Deepa: Spring Boot
(4, 11); -- Deepa: React

-- 6. Insert Initial Mentorship Pairs
-- Pair 1: Arjun Sharma (Google) & Rahul Kumar (CS) -> ACTIVE (1 / 2 slots occupied)
-- Pair 2: Priya Patel (Microsoft) & Ananya Iyer (AI) -> ACTIVE (1 / 1 slots occupied -> FULL!)
INSERT INTO `mentorship_pairs` (`id`, `alumni_id`, `student_id`, `status`, `start_date`, `end_date`, `notes`) VALUES
(1, 1, 1, 'ACTIVE', DATE_SUB(NOW(), INTERVAL 14 DAY), NULL, 'Focus on Java backend engineering and building microservices portfolio'),
(2, 2, 2, 'ACTIVE', DATE_SUB(NOW(), INTERVAL 7 DAY), NULL, 'AI research mentoring and MLOps career roadmap')
ON DUPLICATE KEY UPDATE `status`=VALUES(`status`);

-- 7. Insert Mentoring Sessions
-- Pair 1: Sessions
INSERT INTO `mentoring_sessions` (`id`, `mentorship_pair_id`, `title`, `agenda`, `scheduled_time`, `duration_minutes`, `status`, `feedback`, `completed_at`, `created_at`) VALUES
(1, 1, 'Kickoff & Career Goals Alignment', 'Review academic background, discuss industry expectations and setup project timeline', DATE_SUB(NOW(), INTERVAL 10 DAY), 60, 'COMPLETED', 'Rahul demonstrated strong grasp of core data structures. Next session on Spring Boot REST API patterns.', DATE_SUB(NOW(), INTERVAL 10 DAY), NOW()),
(2, 1, 'Spring Boot Deep Dive & JPA Optimization', 'Hands-on architectural review of REST controllers, Spring Data JPA repositories, and MySQL indexing', DATE_SUB(NOW(), INTERVAL 3 DAY), 60, 'COMPLETED', 'Excellent discussion on N+1 query problem, fetch joins, and connection pooling.', DATE_SUB(NOW(), INTERVAL 3 DAY), NOW()),
(3, 1, 'Microservices System Design & Docker', 'Designing scalable microservices, containerization, and AWS deployment', DATE_ADD(NOW(), INTERVAL 4 DAY), 60, 'SCHEDULED', NULL, NULL, NOW()),

-- Pair 2: Session
(4, 2, 'Modern ML Architecture & Transformers', 'Overview of attention mechanisms, vector databases, and enterprise AI stacks', DATE_SUB(NOW(), INTERVAL 5 DAY), 90, 'COMPLETED', 'Ananya has an outstanding grasp of linear algebra and neural networks. Recommended advanced PyTorch papers.', DATE_SUB(NOW(), INTERVAL 5 DAY), NOW())
ON DUPLICATE KEY UPDATE `status`=VALUES(`status`);
