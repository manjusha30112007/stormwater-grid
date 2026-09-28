package com.mentorconnect.controller;

import com.mentorconnect.dto.MentorshipPairRequestDto;
import com.mentorconnect.dto.MentorshipPairResponseDto;
import com.mentorconnect.entity.MentorshipPairStatus;
import com.mentorconnect.service.MentorshipPairService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/mentorship-pairs")
@CrossOrigin(origins = "*")
public class MentorshipPairController {

    private final MentorshipPairService pairService;

    public MentorshipPairController(MentorshipPairService pairService) {
        this.pairService = pairService;
    }

    /**
     * Match a student with an alumni mentor.
     * Enforces business rule: A mentor cannot be matched beyond their declared maximum concurrent mentee limit.
     */
    @PostMapping
    public ResponseEntity<MentorshipPairResponseDto> createMatch(
            @Valid @RequestBody MentorshipPairRequestDto dto) {
        MentorshipPairResponseDto created = pairService.createMatch(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<MentorshipPairResponseDto>> getAllPairs(
            @RequestParam(required = false) MentorshipPairStatus status) {
        return ResponseEntity.ok(pairService.getAllPairs(status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MentorshipPairResponseDto> getPairById(@PathVariable Long id) {
        return ResponseEntity.ok(pairService.getPairById(id));
    }

    @GetMapping("/alumni/{alumniId}")
    public ResponseEntity<List<MentorshipPairResponseDto>> getPairsByAlumniId(@PathVariable Long alumniId) {
        return ResponseEntity.ok(pairService.getPairsByAlumniId(alumniId));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<MentorshipPairResponseDto>> getPairsByStudentId(@PathVariable Long studentId) {
        return ResponseEntity.ok(pairService.getPairsByStudentId(studentId));
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<MentorshipPairResponseDto> completePair(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body) {
        String notes = body != null ? body.get("notes") : null;
        return ResponseEntity.ok(pairService.completePair(id, notes));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<MentorshipPairResponseDto> cancelPair(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body) {
        String notes = body != null ? body.get("notes") : null;
        return ResponseEntity.ok(pairService.cancelPair(id, notes));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePair(@PathVariable Long id) {
        pairService.deletePair(id);
        return ResponseEntity.noContent().build();
    }
}
