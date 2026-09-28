package com.mentorconnect.service;

import com.mentorconnect.dto.InterestTagDto;
import com.mentorconnect.entity.InterestTag;

import java.util.List;
import java.util.Set;

public interface InterestTagService {
    InterestTagDto createTag(InterestTagDto dto);
    List<InterestTagDto> getAllTags();
    InterestTagDto getTagById(Long id);
    Set<InterestTag> findOrCreateTags(Set<String> tagNames);
    void deleteTag(Long id);
}
