package com.mentorconnect.service;

import com.mentorconnect.dto.StudentRequestDto;
import com.mentorconnect.dto.StudentResponseDto;
import com.mentorconnect.entity.Student;

import java.util.List;

public interface StudentService {
    StudentResponseDto registerStudent(StudentRequestDto dto);
    List<StudentResponseDto> getAllStudents();
    StudentResponseDto getStudentById(Long id);
    StudentResponseDto updateStudent(Long id, StudentRequestDto dto);
    void deleteStudent(Long id);
    StudentResponseDto toDto(Student student);
}
