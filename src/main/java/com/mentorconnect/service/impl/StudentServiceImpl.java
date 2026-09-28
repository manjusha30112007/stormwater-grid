package com.mentorconnect.service.impl;

import com.mentorconnect.dto.InterestTagDto;
import com.mentorconnect.dto.StudentRequestDto;
import com.mentorconnect.dto.StudentResponseDto;
import com.mentorconnect.entity.InterestTag;
import com.mentorconnect.entity.Student;
import com.mentorconnect.exception.DuplicateResourceException;
import com.mentorconnect.exception.ResourceNotFoundException;
import com.mentorconnect.repository.StudentRepository;
import com.mentorconnect.service.InterestTagService;
import com.mentorconnect.service.StudentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final InterestTagService tagService;

    public StudentServiceImpl(StudentRepository studentRepository, InterestTagService tagService) {
        this.studentRepository = studentRepository;
        this.tagService = tagService;
    }

    @Override
    public StudentResponseDto registerStudent(StudentRequestDto dto) {
        if (studentRepository.existsByEmail(dto.getEmail().trim().toLowerCase())) {
            throw new DuplicateResourceException("A student with email '" + dto.getEmail() + "' already exists");
        }

        Student student = new Student();
        student.setName(dto.getName().trim());
        student.setEmail(dto.getEmail().trim().toLowerCase());
        student.setDepartment(dto.getDepartment());
        student.setBatchYear(dto.getBatchYear());

        Set<InterestTag> tags = tagService.findOrCreateTags(dto.getInterestTagNames());
        student.setInterestTags(tags);

        Student saved = studentRepository.save(student);
        return toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentResponseDto> getAllStudents() {
        return studentRepository.findAllWithInterestTags().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public StudentResponseDto getStudentById(Long id) {
        Student student = studentRepository.findByIdWithInterestTags(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + id));
        return toDto(student);
    }

    @Override
    public StudentResponseDto updateStudent(Long id, StudentRequestDto dto) {
        Student student = studentRepository.findByIdWithInterestTags(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + id));

        String newEmail = dto.getEmail().trim().toLowerCase();
        if (!student.getEmail().equalsIgnoreCase(newEmail) && studentRepository.existsByEmail(newEmail)) {
            throw new DuplicateResourceException("Email '" + newEmail + "' is already in use by another student");
        }

        student.setName(dto.getName().trim());
        student.setEmail(newEmail);
        student.setDepartment(dto.getDepartment());
        student.setBatchYear(dto.getBatchYear());

        if (dto.getInterestTagNames() != null) {
            Set<InterestTag> tags = tagService.findOrCreateTags(dto.getInterestTagNames());
            student.setInterestTags(tags);
        }

        Student updated = studentRepository.save(student);
        return toDto(updated);
    }

    @Override
    public void deleteStudent(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Student not found with ID: " + id);
        }
        studentRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public StudentResponseDto toDto(Student student) {
        StudentResponseDto dto = new StudentResponseDto();
        dto.setId(student.getId());
        dto.setName(student.getName());
        dto.setEmail(student.getEmail());
        dto.setDepartment(student.getDepartment());
        dto.setBatchYear(student.getBatchYear());
        dto.setCreatedAt(student.getCreatedAt());

        if (student.getInterestTags() != null) {
            Set<InterestTagDto> tagDtos = student.getInterestTags().stream()
                    .map(t -> new InterestTagDto(t.getId(), t.getName()))
                    .collect(Collectors.toSet());
            dto.setInterestTags(tagDtos);
        }
        return dto;
    }
}
