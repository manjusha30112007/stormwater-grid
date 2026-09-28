package com.mentorconnect.repository;

import com.mentorconnect.entity.Session;
import com.mentorconnect.entity.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
    List<Session> findByMentorshipPairId(Long mentorshipPairId);
    List<Session> findByMentorshipPairIdOrderByScheduledTimeDesc(Long mentorshipPairId);
    List<Session> findByStatus(SessionStatus status);
    long countByStatus(SessionStatus status);
    long countByMentorshipPairId(Long mentorshipPairId);
    long countByMentorshipPairIdAndStatus(Long mentorshipPairId, SessionStatus status);

    @Query("SELECT COALESCE(SUM(s.durationMinutes), 0) FROM Session s WHERE s.mentorshipPair.id = :pairId AND s.status = 'COMPLETED'")
    Long getTotalCompletedMinutesForPair(Long pairId);

    @Query("SELECT COALESCE(SUM(s.durationMinutes), 0) FROM Session s WHERE s.status = 'COMPLETED'")
    Long getTotalPlatformCompletedMinutes();
}
