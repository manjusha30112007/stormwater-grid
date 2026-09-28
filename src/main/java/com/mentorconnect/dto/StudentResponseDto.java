package com.mentorconnect.dto;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class StudentResponseDto {

    private Long id;
    private String name;
    private String email;
    private String department;
    private Integer batchYear;
    private Set<InterestTagDto> interestTags = new HashSet<>();
    private LocalDateTime createdAt;

    public StudentResponseDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Integer getBatchYear() {
        return batchYear;
    }

    public void setBatchYear(Integer batchYear) {
        this.batchYear = batchYear;
    }

    public Set<InterestTagDto> getInterestTags() {
        return interestTags;
    }

    public void setInterestTags(Set<InterestTagDto> interestTags) {
        this.interestTags = interestTags;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
