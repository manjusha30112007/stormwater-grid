package com.mentorconnect.service;

import com.mentorconnect.dto.SessionCompleteDto;
import com.mentorconnect.dto.SessionRequestDto;
import com.mentorconnect.dto.SessionResponseDto;
import com.mentorconnect.entity.Session;
import com.mentorconnect.entity.SessionStatus;

import java.util.List;

public interface SessionService {
    SessionResponseDto scheduleSession(SessionRequestDto dto);
    List<SessionResponseDto> getAllSessions(SessionStatus status, Long mentorshipPairId);
    SessionResponseDto getSessionById(Long id);
    SessionResponseDto completeSession(Long id, SessionCompleteDto dto);
    SessionResponseDto cancelSession(Long id, String reason);
    SessionResponseDto updateSession(Long id, SessionRequestDto dto);
    void deleteSession(Long id);
    SessionResponseDto toDto(Session session);
}
