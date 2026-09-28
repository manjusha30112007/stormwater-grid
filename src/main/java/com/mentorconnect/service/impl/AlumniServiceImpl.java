package com.mentorconnect.service.impl;

import com.mentorconnect.dto.AlumniRequestDto;
import com.mentorconnect.dto.AlumniResponseDto;
import com.mentorconnect.dto.InterestTagDto;
import com.mentorconnect.entity.Alumni;
import com.mentorconnect.entity.InterestTag;
import com.mentorconnect.entity.MentorshipPairStatus;
import com.mentorconnect.exception.DuplicateResourceException;
import com.mentorconnect.exception.ResourceNotFoundException;
import com.mentorconnect.repository.AlumniRepository;
import com.mentorconnect.repository.MentorshipPairRepository;
import com.mentorconnect.service.AlumniService;
import com.mentorconnect.service.InterestTagService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class AlumniServiceImpl implements AlumniService {

    private final AlumniRepository alumniRepository;
    private final MentorshipPairRepository pairRepository;
    private final InterestTagService tagService;

    public AlumniServiceImpl(AlumniRepository alumniRepository,
                             MentorshipPairRepository pairRepository,
                             InterestTagService tagService) {
        this.alumniRepository = alumniRepository;
        this.pairRepository = pairRepository;
        this.tagService = tagService;
    }

    @Override
    public AlumniResponseDto registerAlumni(AlumniRequestDto dto) {
        if (alumniRepository.existsByEmail(dto.getEmail().trim().toLowerCase())) {
            throw new DuplicateResourceException("An alumni with email '" + dto.getEmail() + "' already exists");
        }

        Alumni alumni = new Alumni();
        alumni.setName(dto.getName().trim());
        alumni.setEmail(dto.getEmail().trim().toLowerCase());
        alumni.setCompany(dto.getCompany());
        alumni.setJobTitle(dto.getJobTitle());
        alumni.setGraduationYear(dto.getGraduationYear());
        alumni.setBio(dto.getBio());
        alumni.setMaxMentees(dto.getMaxMentees() != null ? dto.getMaxMentees() : 2);

        Set<InterestTag> tags = tagService.findOrCreateTags(dto.getTagNames());
        alumni.setExpertiseTags(tags);

        Alumni saved = alumniRepository.save(alumni);
        return toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlumniResponseDto> getAllAlumni() {
        return alumniRepository.findAllWithExpertiseTags().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AlumniResponseDto getAlumniById(Long id) {
        Alumni alumni = alumniRepository.findByIdWithExpertiseTags(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alumni mentor not found with ID: " + id));
        return toDto(alumni);
    }

    @Override
    public AlumniResponseDto updateAlumni(Long id, AlumniRequestDto dto) {
        Alumni alumni = alumniRepository.findByIdWithExpertiseTags(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alumni mentor not found with ID: " + id));

        String newEmail = dto.getEmail().trim().toLowerCase();
        if (!alumni.getEmail().equalsIgnoreCase(newEmail) && alumniRepository.existsByEmail(newEmail)) {
            throw new DuplicateResourceException("Email '" + newEmail + "' is already in use by another alumni");
        }

        alumni.setName(dto.getName().trim());
        alumni.setEmail(newEmail);
        alumni.setCompany(dto.getCompany());
        alumni.setJobTitle(dto.getJobTitle());
        alumni.setGraduationYear(dto.getGraduationYear());
        alumni.setBio(dto.getBio());
        if (dto.getMaxMentees() != null) {
            alumni.setMaxMentees(dto.getMaxMentees());
        }

        if (dto.getTagNames() != null) {
            Set<InterestTag> tags = tagService.findOrCreateTags(dto.getTagNames());
            alumni.setExpertiseTags(tags);
        }

        Alumni updated = alumniRepository.save(alumni);
        return toDto(updated);
    }

    @Override
    public void deleteAlumni(Long id) {
        if (!alumniRepository.existsById(id)) {
            throw new ResourceNotFoundException("Alumni mentor not found with ID: " + id);
        }
        alumniRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public AlumniResponseDto toDto(Alumni alumni) {
        AlumniResponseDto dto = new AlumniResponseDto();
        dto.setId(alumni.getId());
        dto.setName(alumni.getName());
        dto.setEmail(alumni.getEmail());
        dto.setCompany(alumni.getCompany());
        dto.setJobTitle(alumni.getJobTitle());
        dto.setGraduationYear(alumni.getGraduationYear());
        dto.setBio(alumni.getBio());
        dto.setMaxMentees(alumni.getMaxMentees());
        dto.setCreatedAt(alumni.getCreatedAt());

        long activeCount = pairRepository.countByAlumniIdAndStatus(alumni.getId(), MentorshipPairStatus.ACTIVE);
        dto.setActiveMenteesCount(activeCount);
        dto.setAvailableSlots(Math.max(0, alumni.getMaxMentees() - activeCount));

        if (alumni.getExpertiseTags() != null) {
            Set<InterestTagDto> tagDtos = alumni.getExpertiseTags().stream()
                    .map(t -> new InterestTagDto(t.getId(), t.getName()))
                    .collect(Collectors.toSet());
            dto.setExpertiseTags(tagDtos);
        }
        return dto;
    }
}
