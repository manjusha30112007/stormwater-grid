package com.mentorconnect.config;

import com.mentorconnect.entity.*;
import com.mentorconnect.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final InterestTagRepository tagRepository;
    private final AlumniRepository alumniRepository;
    private final StudentRepository studentRepository;
    private final MentorshipPairRepository pairRepository;
    private final SessionRepository sessionRepository;

    public DataSeeder(InterestTagRepository tagRepository,
                      AlumniRepository alumniRepository,
                      StudentRepository studentRepository,
                      MentorshipPairRepository pairRepository,
                      SessionRepository sessionRepository) {
        this.tagRepository = tagRepository;
        this.alumniRepository = alumniRepository;
        this.studentRepository = studentRepository;
        this.pairRepository = pairRepository;
        this.sessionRepository = sessionRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (tagRepository.count() > 0 && alumniRepository.count() > 0) {
            log.info("Database already seeded. Skipping initial data seeding.");
            return;
        }

        log.info("Seeding initial data for MentorConnect platform...");

        // 1. Seed Interest Tags
        Map<String, InterestTag> tagMap = new HashMap<>();
        String[] tagNames = {
                "Java", "Spring Boot", "Cloud Computing", "AWS", "Machine Learning",
                "Artificial Intelligence", "Microservices", "System Design",
                "DevOps", "Data Science", "React", "Cybersecurity"
        };

        for (String name : tagNames) {
            tagMap.put(name.toLowerCase(), tagRepository.findByNameIgnoreCase(name)
                    .orElseGet(() -> tagRepository.save(new InterestTag(name))));
        }

        // 2. Seed Alumni Mentors with maxMentees limits and expertise tags
        if (alumniRepository.count() == 0) {
            Alumni a1 = new Alumni("Arjun Sharma", "arjun.sharma@google.com", "Google", "Senior Software Engineer", 2019, 2);
            a1.setBio("Passionate about distributed backend systems, Spring Boot microservices, and high-scale architectures.");
            a1.setExpertiseTags(new HashSet<>(Arrays.asList(
                    tagMap.get("java"), tagMap.get("spring boot"), tagMap.get("microservices"),
                    tagMap.get("system design"), tagMap.get("cloud computing")
            )));
            alumniRepository.save(a1);

            Alumni a2 = new Alumni("Priya Patel", "priya.patel@microsoft.com", "Microsoft", "Lead AI Scientist", 2018, 1);
            a2.setBio("Specializing in Deep Learning, NLP, and LLM fine-tuning pipelines. High demand mentor with 1 mentee max slot.");
            a2.setExpertiseTags(new HashSet<>(Arrays.asList(
                    tagMap.get("machine learning"), tagMap.get("artificial intelligence"), tagMap.get("data science")
            )));
            alumniRepository.save(a2);

            Alumni a3 = new Alumni("Rohit Verma", "rohit.verma@amazon.com", "Amazon", "Principal Cloud Architect", 2017, 2);
            a3.setBio("AWS Solutions Architect with 8+ years designing fault-tolerant cloud infrastructures and CI/CD pipelines.");
            a3.setExpertiseTags(new HashSet<>(Arrays.asList(
                    tagMap.get("cloud computing"), tagMap.get("aws"), tagMap.get("devops"), tagMap.get("microservices")
            )));
            alumniRepository.save(a3);

            Alumni a4 = new Alumni("Sneha Rao", "sneha.rao@stripe.com", "Stripe", "Staff Fullstack Engineer", 2020, 1);
            a4.setBio("Building high-reliability payment workflows using Java backends and modern React frontends.");
            a4.setExpertiseTags(new HashSet<>(Arrays.asList(
                    tagMap.get("java"), tagMap.get("react"), tagMap.get("spring boot"), tagMap.get("system design")
            )));
            alumniRepository.save(a4);

            // 3. Seed Students with interest tags
            Student s1 = new Student("Rahul Kumar", "rahul.kumar@college.edu", "Computer Science", 2026);
            s1.setInterestTags(new HashSet<>(Arrays.asList(
                    tagMap.get("java"), tagMap.get("spring boot"), tagMap.get("system design")
            )));
            studentRepository.save(s1);

            Student s2 = new Student("Ananya Iyer", "ananya.iyer@college.edu", "Artificial Intelligence", 2026);
            s2.setInterestTags(new HashSet<>(Arrays.asList(
                    tagMap.get("machine learning"), tagMap.get("artificial intelligence"), tagMap.get("data science")
            )));
            studentRepository.save(s2);

            Student s3 = new Student("Vikram Singh", "vikram.singh@college.edu", "Information Technology", 2027);
            s3.setInterestTags(new HashSet<>(Arrays.asList(
                    tagMap.get("cloud computing"), tagMap.get("aws"), tagMap.get("devops")
            )));
            studentRepository.save(s3);

            Student s4 = new Student("Deepa Menon", "deepa.menon@college.edu", "Computer Science", 2027);
            s4.setInterestTags(new HashSet<>(Arrays.asList(
                    tagMap.get("java"), tagMap.get("react"), tagMap.get("spring boot")
            )));
            studentRepository.save(s4);

            // 4. Seed Mentorship Pairs
            // Pair 1: Arjun Sharma (Google) & Rahul Kumar (CS) -> ACTIVE (1 / 2 slots filled)
            MentorshipPair p1 = new MentorshipPair(a1, s1, "Focus on Java backend engineering and building microservices");
            p1 = pairRepository.save(p1);

            // Pair 2: Priya Patel (Microsoft) & Ananya Iyer (AI) -> ACTIVE (1 / 1 slots filled -> AT FULL CAPACITY!)
            MentorshipPair p2 = new MentorshipPair(a2, s2, "AI research mentoring and MLOps career roadmap");
            p2 = pairRepository.save(p2);

            // 5. Seed Mentoring Sessions
            // P1 Completed Session
            Session sess1 = new Session(p1, "Kickoff & Career Goals Alignment",
                    "Review academic background, discuss industry expectations and setup project timeline",
                    LocalDateTime.now().minusDays(10), 60);
            sess1.setStatus(SessionStatus.COMPLETED);
            sess1.setCompletedAt(LocalDateTime.now().minusDays(10).plusMinutes(60));
            sess1.setFeedback("Rahul demonstrated strong grasp of core data structures. Next session on Spring Boot REST API patterns.");
            sessionRepository.save(sess1);

            // P1 Completed Session 2
            Session sess2 = new Session(p1, "Spring Boot Deep Dive & JPA Optimization",
                    "Hands-on architectural review of REST controllers, Spring Data JPA repositories, and MySQL indexing",
                    LocalDateTime.now().minusDays(3), 60);
            sess2.setStatus(SessionStatus.COMPLETED);
            sess2.setCompletedAt(LocalDateTime.now().minusDays(3).plusMinutes(60));
            sess2.setFeedback("Excellent discussion on N+1 query problem, fetch joins, and connection pooling.");
            sessionRepository.save(sess2);

            // P1 Scheduled Session
            Session sess3 = new Session(p1, "Microservices System Design & Docker",
                    "Designing scalable microservices, containerization, and AWS deployment",
                    LocalDateTime.now().plusDays(4).withHour(18).withMinute(0).withSecond(0), 60);
            sess3.setStatus(SessionStatus.SCHEDULED);
            sessionRepository.save(sess3);

            // P2 Completed Session
            Session sess4 = new Session(p2, "Modern ML Architecture & Transformers",
                    "Overview of attention mechanisms, vector databases, and enterprise AI stacks",
                    LocalDateTime.now().minusDays(5), 90);
            sess4.setStatus(SessionStatus.COMPLETED);
            sess4.setCompletedAt(LocalDateTime.now().minusDays(5).plusMinutes(90));
            sess4.setFeedback("Ananya has an outstanding grasp of linear algebra and neural networks. Recommended advanced PyTorch papers.");
            sessionRepository.save(sess4);

            log.info("Sample seed data initialized successfully! Total Tags: {}, Alumni: 4, Students: 4, Pairs: 2, Sessions: 4",
                    tagMap.size());
        }
    }
}
