package com.mentorconnect.repository;

import com.mentorconnect.entity.MentorshipPair;
import com.mentorconnect.entity.MentorshipPairStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MentorshipPairRepository extends JpaRepository<MentorshipPair, Long> {
    long countByAlumniIdAndStatus(Long alumniId, MentorshipPairStatus status);
    boolean existsByAlumniIdAndStudentIdAndStatus(Long alumniId, Long studentId, MentorshipPairStatus status);
    List<MentorshipPair> findByAlumniId(Long alumniId);
    List<MentorshipPair> findByStudentId(Long studentId);
    List<MentorshipPair> findByStatus(MentorshipPairStatus status);
    long countByStatus(MentorshipPairStatus status);
}
