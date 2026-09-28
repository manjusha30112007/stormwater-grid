package com.mentorconnect.service.impl;

import com.mentorconnect.dto.MentorshipPairRequestDto;
import com.mentorconnect.dto.MentorshipPairResponseDto;
import com.mentorconnect.entity.*;
import com.mentorconnect.exception.DuplicateResourceException;
import com.mentorconnect.exception.MentorLimitExceededException;
import com.mentorconnect.exception.ResourceNotFoundException;
import com.mentorconnect.repository.AlumniRepository;
import com.mentorconnect.repository.MentorshipPairRepository;
import com.mentorconnect.repository.SessionRepository;
import com.mentorconnect.repository.StudentRepository;
import com.mentorconnect.service.MentorshipPairService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MentorshipPairServiceImpl implements MentorshipPairService {

    private final MentorshipPairRepository pairRepository;
    private final AlumniRepository alumniRepository;
    private final StudentRepository studentRepository;
    private final SessionRepository sessionRepository;

    public MentorshipPairServiceImpl(MentorshipPairRepository pairRepository,
                                     AlumniRepository alumniRepository,
                                     StudentRepository studentRepository,
                                     SessionRepository sessionRepository) {
        this.pairRepository = pairRepository;
        this.alumniRepository = alumniRepository;
        this.studentRepository = studentRepository;
        this.sessionRepository = sessionRepository;
    }

    @Override
    public MentorshipPairResponseDto createMatch(MentorshipPairRequestDto dto) {
        Alumni alumni = alumniRepository.findById(dto.getAlumniId())
                .orElseThrow(() -> new ResourceNotFoundException("Alumni mentor not found with ID: " + dto.getAlumniId()));

        Student student = studentRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + dto.getStudentId()));

        // Check if student is already actively matched with this mentor
        if (pairRepository.existsByAlumniIdAndStudentIdAndStatus(alumni.getId(), student.getId(), MentorshipPairStatus.ACTIVE)) {
            throw new DuplicateResourceException("Student '" + student.getName() + "' is already actively matched with mentor '" + alumni.getName() + "'");
        }

        // BUSINESS RULE: A mentor cannot be matched beyond their declared maximum concurrent mentee limit.
        long activeCount = pairRepository.countByAlumniIdAndStatus(alumni.getId(), MentorshipPairStatus.ACTIVE);
        if (activeCount >= alumni.getMaxMentees()) {
            throw new MentorLimitExceededException(
                    "Mentor '" + alumni.getName() + "' has reached their declared maximum concurrent mentee limit of "
                            + alumni.getMaxMentees() + " (Current active mentees: " + activeCount + "). Matching rejected.");
        }

        MentorshipPair pair = new MentorshipPair();
        pair.setAlumni(alumni);
        pair.setStudent(student);
        pair.setStatus(MentorshipPairStatus.ACTIVE);
        pair.setStartDate(LocalDateTime.now());
        pair.setNotes(dto.getNotes());

        MentorshipPair saved = pairRepository.save(pair);
        return toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MentorshipPairResponseDto> getAllPairs(MentorshipPairStatus status) {
        List<MentorshipPair> pairs = (status != null)
                ? pairRepository.findByStatus(status)
                : pairRepository.findAll();

        return pairs.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public MentorshipPairResponseDto getPairById(Long id) {
        MentorshipPair pair = pairRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mentorship pair not found with ID: " + id));
        return toDto(pair);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MentorshipPairResponseDto> getPairsByAlumniId(Long alumniId) {
        if (!alumniRepository.existsById(alumniId)) {
            throw new ResourceNotFoundException("Alumni mentor not found with ID: " + alumniId);
        }
        return pairRepository.findByAlumniId(alumniId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MentorshipPairResponseDto> getPairsByStudentId(Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Student not found with ID: " + studentId);
        }
        return pairRepository.findByStudentId(studentId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public MentorshipPairResponseDto completePair(Long id, String notes) {
        MentorshipPair pair = pairRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mentorship pair not found with ID: " + id));

        pair.setStatus(MentorshipPairStatus.COMPLETED);
        pair.setEndDate(LocalDateTime.now());
        if (notes != null && !notes.trim().isEmpty()) {
            pair.setNotes((pair.getNotes() != null ? pair.getNotes() + " | " : "") + notes.trim());
        }

        return toDto(pairRepository.save(pair));
    }

    @Override
    public MentorshipPairResponseDto cancelPair(Long id, String notes) {
        MentorshipPair pair = pairRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mentorship pair not found with ID: " + id));

        pair.setStatus(MentorshipPairStatus.CANCELLED);
        pair.setEndDate(LocalDateTime.now());
        if (notes != null && !notes.trim().isEmpty()) {
            pair.setNotes((pair.getNotes() != null ? pair.getNotes() + " | " : "") + notes.trim());
        }

        return toDto(pairRepository.save(pair));
    }

    @Override
    public void deletePair(Long id) {
        if (!pairRepository.existsById(id)) {
            throw new ResourceNotFoundException("Mentorship pair not found with ID: " + id);
        }
        pairRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public MentorshipPairResponseDto toDto(MentorshipPair pair) {
        MentorshipPairResponseDto dto = new MentorshipPairResponseDto();
        dto.setId(pair.getId());
        dto.setAlumniId(pair.getAlumni().getId());
        dto.setAlumniName(pair.getAlumni().getName());
        dto.setAlumniEmail(pair.getAlumni().getEmail());
        dto.setAlumniCompany(pair.getAlumni().getCompany());
        dto.setStudentId(pair.getStudent().getId());
        dto.setStudentName(pair.getStudent().getName());
        dto.setStudentEmail(pair.getStudent().getEmail());
        dto.setStudentDepartment(pair.getStudent().getDepartment());
        dto.setStatus(pair.getStatus());
        dto.setStartDate(pair.getStartDate());
        dto.setEndDate(pair.getEndDate());
        dto.setNotes(pair.getNotes());

        long totalSessions = sessionRepository.countByMentorshipPairId(pair.getId());
        long completedSessions = sessionRepository.countByMentorshipPairIdAndStatus(pair.getId(), SessionStatus.COMPLETED);
        dto.setTotalSessions(totalSessions);
        dto.setCompletedSessions(completedSessions);

        return dto;
    }
}
