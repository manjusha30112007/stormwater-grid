package com.mentorconnect.service.impl;

import com.mentorconnect.dto.MatchSuggestionDto;
import com.mentorconnect.entity.Alumni;
import com.mentorconnect.entity.InterestTag;
import com.mentorconnect.entity.MentorshipPairStatus;
import com.mentorconnect.entity.Student;
import com.mentorconnect.exception.ResourceNotFoundException;
import com.mentorconnect.repository.AlumniRepository;
import com.mentorconnect.repository.MentorshipPairRepository;
import com.mentorconnect.repository.StudentRepository;
import com.mentorconnect.service.MatchingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class MatchingServiceImpl implements MatchingService {

    private final AlumniRepository alumniRepository;
    private final StudentRepository studentRepository;
    private final MentorshipPairRepository pairRepository;

    public MatchingServiceImpl(AlumniRepository alumniRepository,
                               StudentRepository studentRepository,
                               MentorshipPairRepository pairRepository) {
        this.alumniRepository = alumniRepository;
        this.studentRepository = studentRepository;
        this.pairRepository = pairRepository;
    }

    @Override
    public List<MatchSuggestionDto> suggestMentorsForStudent(Long studentId) {
        Student student = studentRepository.findByIdWithInterestTags(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + studentId));

        Set<String> studentTagNames = student.getInterestTags().stream()
                .map(t -> t.getName().toLowerCase())
                .collect(Collectors.toSet());

        List<Alumni> allAlumni = alumniRepository.findAllWithExpertiseTags();
        List<MatchSuggestionDto> suggestions = new ArrayList<>();

        for (Alumni alumni : allAlumni) {
            // Check current active mentees count
            long activeMentees = pairRepository.countByAlumniIdAndStatus(alumni.getId(), MentorshipPairStatus.ACTIVE);
            long availableSlots = alumni.getMaxMentees() - activeMentees;

            // Check if student already has active match with this mentor
            boolean alreadyMatched = pairRepository.existsByAlumniIdAndStudentIdAndStatus(
                    alumni.getId(), student.getId(), MentorshipPairStatus.ACTIVE);

            // Skip mentors who have no available capacity or are already active mentors for this student
            if (availableSlots <= 0 || alreadyMatched) {
                continue;
            }

            // Calculate tag overlap
            Set<String> alumniTagNames = alumni.getExpertiseTags().stream()
                    .map(t -> t.getName().toLowerCase())
                    .collect(Collectors.toSet());

            Set<String> overlappingTags = new HashSet<>(studentTagNames);
            overlappingTags.retainAll(alumniTagNames);

            int overlapScore = overlappingTags.size();

            Set<String> allExpertiseNames = alumni.getExpertiseTags().stream()
                    .map(InterestTag::getName)
                    .collect(Collectors.toSet());

            // Convert overlapping tag lowercase back to display names
            Set<String> displayOverlappingTags = alumni.getExpertiseTags().stream()
                    .filter(t -> studentTagNames.contains(t.getName().toLowerCase()))
                    .map(InterestTag::getName)
                    .collect(Collectors.toSet());

            suggestions.add(new MatchSuggestionDto(
                    alumni.getId(),
                    alumni.getName(),
                    alumni.getEmail(),
                    alumni.getCompany(),
                    alumni.getJobTitle(),
                    alumni.getMaxMentees(),
                    activeMentees,
                    availableSlots,
                    overlapScore,
                    displayOverlappingTags,
                    allExpertiseNames
            ));
        }

        // Rank suggestions: Highest tag overlap first, then available slots descending, then name ascending
        suggestions.sort((a, b) -> {
            int scoreCompare = Integer.compare(b.getOverlapScore(), a.getOverlapScore());
            if (scoreCompare != 0) return scoreCompare;
            int slotsCompare = Long.compare(b.getAvailableSlots(), a.getAvailableSlots());
            if (slotsCompare != 0) return slotsCompare;
            return a.getName().compareToIgnoreCase(b.getName());
        });

        return suggestions;
    }

    @Override
    public List<MatchSuggestionDto> suggestMentorsByTags(Set<String> tagNames) {
        Set<String> searchTags = (tagNames == null) ? Collections.emptySet() :
                tagNames.stream()
                        .filter(Objects::nonNull)
                        .map(s -> s.trim().toLowerCase())
                        .collect(Collectors.toSet());

        List<Alumni> allAlumni = alumniRepository.findAllWithExpertiseTags();
        List<MatchSuggestionDto> suggestions = new ArrayList<>();

        for (Alumni alumni : allAlumni) {
            long activeMentees = pairRepository.countByAlumniIdAndStatus(alumni.getId(), MentorshipPairStatus.ACTIVE);
            long availableSlots = alumni.getMaxMentees() - activeMentees;

            // Mentor cannot be matched beyond max concurrent mentees
            if (availableSlots <= 0) {
                continue;
            }

            Set<String> alumniTagNames = alumni.getExpertiseTags().stream()
                    .map(t -> t.getName().toLowerCase())
                    .collect(Collectors.toSet());

            Set<String> overlapping = new HashSet<>(searchTags);
            overlapping.retainAll(alumniTagNames);

            int overlapScore = overlapping.size();

            Set<String> allExpertiseNames = alumni.getExpertiseTags().stream()
                    .map(InterestTag::getName)
                    .collect(Collectors.toSet());

            Set<String> displayOverlappingTags = alumni.getExpertiseTags().stream()
                    .filter(t -> searchTags.contains(t.getName().toLowerCase()))
                    .map(InterestTag::getName)
                    .collect(Collectors.toSet());

            suggestions.add(new MatchSuggestionDto(
                    alumni.getId(),
                    alumni.getName(),
                    alumni.getEmail(),
                    alumni.getCompany(),
                    alumni.getJobTitle(),
                    alumni.getMaxMentees(),
                    activeMentees,
                    availableSlots,
                    overlapScore,
                    displayOverlappingTags,
                    allExpertiseNames
            ));
        }

        suggestions.sort((a, b) -> {
            int scoreCompare = Integer.compare(b.getOverlapScore(), a.getOverlapScore());
            if (scoreCompare != 0) return scoreCompare;
            int slotsCompare = Long.compare(b.getAvailableSlots(), a.getAvailableSlots());
            if (slotsCompare != 0) return slotsCompare;
            return a.getName().compareToIgnoreCase(b.getName());
        });

        return suggestions;
    }
}
