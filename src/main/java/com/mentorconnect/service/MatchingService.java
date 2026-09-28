package com.mentorconnect.service;

import com.mentorconnect.dto.MatchSuggestionDto;

import java.util.List;
import java.util.Set;

public interface MatchingService {
    List<MatchSuggestionDto> suggestMentorsForStudent(Long studentId);
    List<MatchSuggestionDto> suggestMentorsByTags(Set<String> tagNames);
}
