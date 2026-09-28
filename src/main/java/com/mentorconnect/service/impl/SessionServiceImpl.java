package com.mentorconnect.service.impl;

import com.mentorconnect.dto.SessionCompleteDto;
import com.mentorconnect.dto.SessionRequestDto;
import com.mentorconnect.dto.SessionResponseDto;
import com.mentorconnect.entity.MentorshipPair;
import com.mentorconnect.entity.MentorshipPairStatus;
import com.mentorconnect.entity.Session;
import com.mentorconnect.entity.SessionStatus;
import com.mentorconnect.exception.InvalidOperationException;
import com.mentorconnect.exception.ResourceNotFoundException;
import com.mentorconnect.repository.MentorshipPairRepository;
import com.mentorconnect.repository.SessionRepository;
import com.mentorconnect.service.SessionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class SessionServiceImpl implements SessionService {

    private final SessionRepository sessionRepository;
    private final MentorshipPairRepository pairRepository;

    public SessionServiceImpl(SessionRepository sessionRepository, MentorshipPairRepository pairRepository) {
        this.sessionRepository = sessionRepository;
        this.pairRepository = pairRepository;
    }

    @Override
    public SessionResponseDto scheduleSession(SessionRequestDto dto) {
        MentorshipPair pair = pairRepository.findById(dto.getMentorshipPairId())
                .orElseThrow(() -> new ResourceNotFoundException("Mentorship pair not found with ID: " + dto.getMentorshipPairId()));

        if (pair.getStatus() != MentorshipPairStatus.ACTIVE) {
            throw new InvalidOperationException("Cannot schedule a session for a mentorship pair that is not ACTIVE (current status: " + pair.getStatus() + ")");
        }

        Session session = new Session();
        session.setMentorshipPair(pair);
        session.setTitle(dto.getTitle().trim());
        session.setAgenda(dto.getAgenda());
        session.setScheduledTime(dto.getScheduledTime());
        session.setDurationMinutes(dto.getDurationMinutes() != null ? dto.getDurationMinutes() : 60);
        session.setStatus(SessionStatus.SCHEDULED);

        Session saved = sessionRepository.save(session);
        return toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SessionResponseDto> getAllSessions(SessionStatus status, Long mentorshipPairId) {
        List<Session> sessions;
        if (mentorshipPairId != null) {
            sessions = sessionRepository.findByMentorshipPairIdOrderByScheduledTimeDesc(mentorshipPairId);
            if (status != null) {
                sessions = sessions.stream().filter(s -> s.getStatus() == status).collect(Collectors.toList());
            }
        } else if (status != null) {
            sessions = sessionRepository.findByStatus(status);
        } else {
            sessions = sessionRepository.findAll();
        }

        return sessions.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public SessionResponseDto getSessionById(Long id) {
        Session session = sessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found with ID: " + id));
        return toDto(session);
    }

    @Override
    public SessionResponseDto completeSession(Long id, SessionCompleteDto dto) {
        Session session = sessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found with ID: " + id));

        if (session.getStatus() == SessionStatus.COMPLETED) {
            throw new InvalidOperationException("Session is already marked as COMPLETED");
        }
        if (session.getStatus() == SessionStatus.CANCELLED) {
            throw new InvalidOperationException("Cannot complete a CANCELLED session");
        }

        session.setStatus(SessionStatus.COMPLETED);
        session.setCompletedAt(dto != null && dto.getCompletedAt() != null ? dto.getCompletedAt() : LocalDateTime.now());
        if (dto != null && dto.getFeedback() != null) {
            session.setFeedback(dto.getFeedback().trim());
        }

        return toDto(sessionRepository.save(session));
    }

    @Override
    public SessionResponseDto cancelSession(Long id, String reason) {
        Session session = sessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found with ID: " + id));

        if (session.getStatus() == SessionStatus.COMPLETED) {
            throw new InvalidOperationException("Cannot cancel a session that is already COMPLETED");
        }

        session.setStatus(SessionStatus.CANCELLED);
        if (reason != null && !reason.trim().isEmpty()) {
            session.setFeedback((session.getFeedback() != null ? session.getFeedback() + " | Cancellation Reason: " : "Cancellation Reason: ") + reason.trim());
        }

        return toDto(sessionRepository.save(session));
    }

    @Override
    public SessionResponseDto updateSession(Long id, SessionRequestDto dto) {
        Session session = sessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found with ID: " + id));

        session.setTitle(dto.getTitle().trim());
        session.setAgenda(dto.getAgenda());
        session.setScheduledTime(dto.getScheduledTime());
        session.setDurationMinutes(dto.getDurationMinutes());

        return toDto(sessionRepository.save(session));
    }

    @Override
    public void deleteSession(Long id) {
        if (!sessionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Session not found with ID: " + id);
        }
        sessionRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public SessionResponseDto toDto(Session session) {
        SessionResponseDto dto = new SessionResponseDto();
        dto.setId(session.getId());
        dto.setMentorshipPairId(session.getMentorshipPair().getId());
        dto.setMentorId(session.getMentorshipPair().getAlumni().getId());
        dto.setMentorName(session.getMentorshipPair().getAlumni().getName());
        dto.setStudentId(session.getMentorshipPair().getStudent().getId());
        dto.setStudentName(session.getMentorshipPair().getStudent().getName());
        dto.setTitle(session.getTitle());
        dto.setAgenda(session.getAgenda());
        dto.setScheduledTime(session.getScheduledTime());
        dto.setDurationMinutes(session.getDurationMinutes());
        dto.setStatus(session.getStatus());
        dto.setFeedback(session.getFeedback());
        dto.setCompletedAt(session.getCompletedAt());
        dto.setCreatedAt(session.getCreatedAt());
        return dto;
    }
}
