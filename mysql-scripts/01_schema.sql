-- ========================================================
-- MentorConnect - Alumni-Student Mentorship Matching Platform
-- Database Schema Definition (DDL) for MySQL Workbench
-- ========================================================

CREATE DATABASE IF NOT EXISTS `mentorconnect`
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE `mentorconnect`;

-- Disable foreign key checks for clean recreation if needed
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `mentoring_sessions`;
DROP TABLE IF EXISTS `mentorship_pairs`;
DROP TABLE IF EXISTS `student_interest_tags`;
DROP TABLE IF EXISTS `alumni_expertise_tags`;
DROP TABLE IF EXISTS `students`;
DROP TABLE IF EXISTS `alumni`;
DROP TABLE IF EXISTS `interest_tags`;

SET FOREIGN_KEY_CHECKS = 1;

-- --------------------------------------------------------
-- Table: interest_tags
-- Stores technical and domain interest/expertise tags
-- --------------------------------------------------------
CREATE TABLE `interest_tags` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(100) NOT NULL UNIQUE,
    INDEX `idx_tag_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------
-- Table: alumni
-- Stores alumni mentor profile with declared max mentee limit
-- --------------------------------------------------------
CREATE TABLE `alumni` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(255) NOT NULL,
    `email` VARCHAR(255) NOT NULL UNIQUE,
    `company` VARCHAR(255),
    `job_title` VARCHAR(255),
    `graduation_year` INT,
    `bio` VARCHAR(1000),
    `max_mentees` INT NOT NULL DEFAULT 2,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT `chk_alumni_max_mentees` CHECK (`max_mentees` >= 1),
    INDEX `idx_alumni_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------
-- Table: students
-- Stores current student mentees specifying academic info
-- --------------------------------------------------------
CREATE TABLE `students` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(255) NOT NULL,
    `email` VARCHAR(255) NOT NULL UNIQUE,
    `department` VARCHAR(255),
    `batch_year` INT,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX `idx_student_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------
-- Table: alumni_expertise_tags (Join Table)
-- Associates Alumni mentors with their expertise areas
-- --------------------------------------------------------
CREATE TABLE `alumni_expertise_tags` (
    `alumni_id` BIGINT NOT NULL,
    `tag_id` BIGINT NOT NULL,
    PRIMARY KEY (`alumni_id`, `tag_id`),
    CONSTRAINT `fk_aet_alumni` FOREIGN KEY (`alumni_id`) REFERENCES `alumni` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_aet_tag` FOREIGN KEY (`tag_id`) REFERENCES `interest_tags` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------
-- Table: student_interest_tags (Join Table)
-- Associates Students with their desired mentorship topics
-- --------------------------------------------------------
CREATE TABLE `student_interest_tags` (
    `student_id` BIGINT NOT NULL,
    `tag_id` BIGINT NOT NULL,
    PRIMARY KEY (`student_id`, `tag_id`),
    CONSTRAINT `fk_sit_student` FOREIGN KEY (`student_id`) REFERENCES `students` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_sit_tag` FOREIGN KEY (`tag_id`) REFERENCES `interest_tags` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------
-- Table: mentorship_pairs
-- Tracks mentor-mentee relationship and lifecycle status
-- --------------------------------------------------------
CREATE TABLE `mentorship_pairs` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `alumni_id` BIGINT NOT NULL,
    `student_id` BIGINT NOT NULL,
    `status` VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    `start_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `end_date` DATETIME NULL,
    `notes` VARCHAR(1000) NULL,
    CONSTRAINT `fk_pair_alumni` FOREIGN KEY (`alumni_id`) REFERENCES `alumni` (`id`) ON DELETE RESTRICT,
    CONSTRAINT `fk_pair_student` FOREIGN KEY (`student_id`) REFERENCES `students` (`id`) ON DELETE RESTRICT,
    INDEX `idx_pair_alumni_status` (`alumni_id`, `status`),
    INDEX `idx_pair_student_status` (`student_id`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------
-- Table: mentoring_sessions
-- Tracks scheduled sessions, completion status, and notes
-- --------------------------------------------------------
CREATE TABLE `mentoring_sessions` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `mentorship_pair_id` BIGINT NOT NULL,
    `title` VARCHAR(255) NOT NULL,
    `agenda` VARCHAR(1000) NULL,
    `scheduled_time` DATETIME NOT NULL,
    `duration_minutes` INT NOT NULL DEFAULT 60,
    `status` VARCHAR(30) NOT NULL DEFAULT 'SCHEDULED',
    `feedback` VARCHAR(1000) NULL,
    `completed_at` DATETIME NULL,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT `fk_session_pair` FOREIGN KEY (`mentorship_pair_id`) REFERENCES `mentorship_pairs` (`id`) ON DELETE CASCADE,
    INDEX `idx_session_pair_status` (`mentorship_pair_id`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
