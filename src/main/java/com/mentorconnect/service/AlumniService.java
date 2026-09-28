package com.mentorconnect.service;

import com.mentorconnect.dto.AlumniRequestDto;
import com.mentorconnect.dto.AlumniResponseDto;
import com.mentorconnect.entity.Alumni;

import java.util.List;

public interface AlumniService {
    AlumniResponseDto registerAlumni(AlumniRequestDto dto);
    List<AlumniResponseDto> getAllAlumni();
    AlumniResponseDto getAlumniById(Long id);
    AlumniResponseDto updateAlumni(Long id, AlumniRequestDto dto);
    void deleteAlumni(Long id);
    AlumniResponseDto toDto(Alumni alumni);
}
