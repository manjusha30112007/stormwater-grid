package com.mentorconnect.controller;

import com.mentorconnect.dto.SessionCompleteDto;
import com.mentorconnect.dto.SessionRequestDto;
import com.mentorconnect.dto.SessionResponseDto;
import com.mentorconnect.entity.SessionStatus;
import com.mentorconnect.service.SessionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sessions")
@CrossOrigin(origins = "*")
public class SessionController {

    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @PostMapping
    public ResponseEntity<SessionResponseDto> scheduleSession(@Valid @RequestBody SessionRequestDto dto) {
        SessionResponseDto created = sessionService.scheduleSession(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<SessionResponseDto>> getAllSessions(
            @RequestParam(required = false) SessionStatus status,
            @RequestParam(required = false) Long mentorshipPairId) {
        return ResponseEntity.ok(sessionService.getAllSessions(status, mentorshipPairId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SessionResponseDto> getSessionById(@PathVariable Long id) {
        return ResponseEntity.ok(sessionService.getSessionById(id));
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<SessionResponseDto> completeSession(
            @PathVariable Long id,
            @RequestBody(required = false) SessionCompleteDto dto) {
        return ResponseEntity.ok(sessionService.completeSession(id, dto));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<SessionResponseDto> cancelSession(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body) {
        String reason = body != null ? body.get("reason") : null;
        return ResponseEntity.ok(sessionService.cancelSession(id, reason));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SessionResponseDto> updateSession(
            @PathVariable Long id,
            @Valid @RequestBody SessionRequestDto dto) {
        return ResponseEntity.ok(sessionService.updateSession(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSession(@PathVariable Long id) {
        sessionService.deleteSession(id);
        return ResponseEntity.noContent().build();
    }
}
