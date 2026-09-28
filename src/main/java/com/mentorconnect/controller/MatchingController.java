package com.mentorconnect.controller;

import com.mentorconnect.dto.MatchSuggestionDto;
import com.mentorconnect.service.MatchingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/matching")
@CrossOrigin(origins = "*")
public class MatchingController {

    private final MatchingService matchingService;

    public MatchingController(MatchingService matchingService) {
        this.matchingService = matchingService;
    }

    /**
     * Auto-suggest best-matching mentors for a given student based on tag overlap.
     * Ranked by number of overlapping interest tags (highest first).
     * Automatically filters out mentors who reached their max concurrent mentees limit.
     */
    @GetMapping("/suggestions/{studentId}")
    public ResponseEntity<List<MatchSuggestionDto>> getSuggestionsForStudent(@PathVariable Long studentId) {
        List<MatchSuggestionDto> suggestions = matchingService.suggestMentorsForStudent(studentId);
        return ResponseEntity.ok(suggestions);
    }

    /**
     * Auto-suggest best-matching mentors based on arbitrary interest tags.
     */
    @PostMapping("/suggestions-by-tags")
    public ResponseEntity<List<MatchSuggestionDto>> getSuggestionsByTags(@RequestBody Set<String> tagNames) {
        List<MatchSuggestionDto> suggestions = matchingService.suggestMentorsByTags(tagNames);
        return ResponseEntity.ok(suggestions);
    }
}
