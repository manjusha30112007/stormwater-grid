package com.mentorconnect.service;

import com.mentorconnect.dto.MentorshipPairRequestDto;
import com.mentorconnect.dto.MentorshipPairResponseDto;
import com.mentorconnect.entity.MentorshipPair;
import com.mentorconnect.entity.MentorshipPairStatus;

import java.util.List;

public interface MentorshipPairService {
    MentorshipPairResponseDto createMatch(MentorshipPairRequestDto dto);
    List<MentorshipPairResponseDto> getAllPairs(MentorshipPairStatus status);
    MentorshipPairResponseDto getPairById(Long id);
    List<MentorshipPairResponseDto> getPairsByAlumniId(Long alumniId);
    List<MentorshipPairResponseDto> getPairsByStudentId(Long studentId);
    MentorshipPairResponseDto completePair(Long id, String notes);
    MentorshipPairResponseDto cancelPair(Long id, String notes);
    void deletePair(Long id);
    MentorshipPairResponseDto toDto(MentorshipPair pair);
}
