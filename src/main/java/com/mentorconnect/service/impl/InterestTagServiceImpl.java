package com.mentorconnect.service.impl;

import com.mentorconnect.dto.InterestTagDto;
import com.mentorconnect.entity.InterestTag;
import com.mentorconnect.exception.DuplicateResourceException;
import com.mentorconnect.exception.ResourceNotFoundException;
import com.mentorconnect.repository.InterestTagRepository;
import com.mentorconnect.service.InterestTagService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class InterestTagServiceImpl implements InterestTagService {

    private final InterestTagRepository tagRepository;

    public InterestTagServiceImpl(InterestTagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    public InterestTagDto createTag(InterestTagDto dto) {
        String cleanName = dto.getName().trim();
        if (tagRepository.existsByNameIgnoreCase(cleanName)) {
            throw new DuplicateResourceException("Interest tag '" + cleanName + "' already exists");
        }
        InterestTag tag = tagRepository.save(new InterestTag(cleanName));
        return new InterestTagDto(tag.getId(), tag.getName());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InterestTagDto> getAllTags() {
        return tagRepository.findAll().stream()
                .map(t -> new InterestTagDto(t.getId(), t.getName()))
                .sorted(Comparator.comparing(InterestTagDto::getName))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public InterestTagDto getTagById(Long id) {
        InterestTag tag = tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Interest tag not found with ID: " + id));
        return new InterestTagDto(tag.getId(), tag.getName());
    }

    @Override
    public Set<InterestTag> findOrCreateTags(Set<String> tagNames) {
        if (tagNames == null || tagNames.isEmpty()) {
            return new HashSet<>();
        }
        Set<InterestTag> tags = new HashSet<>();
        for (String rawName : tagNames) {
            if (rawName == null || rawName.trim().isEmpty()) continue;
            String trimmed = rawName.trim();
            InterestTag tag = tagRepository.findByNameIgnoreCase(trimmed)
                    .orElseGet(() -> tagRepository.save(new InterestTag(trimmed)));
            tags.add(tag);
        }
        return tags;
    }

    @Override
    public void deleteTag(Long id) {
        if (!tagRepository.existsById(id)) {
            throw new ResourceNotFoundException("Interest tag not found with ID: " + id);
        }
        tagRepository.deleteById(id);
    }
}
