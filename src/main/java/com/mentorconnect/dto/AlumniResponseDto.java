package com.mentorconnect.dto;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class AlumniResponseDto {

    private Long id;
    private String name;
    private String email;
    private String company;
    private String jobTitle;
    private Integer graduationYear;
    private String bio;
    private Integer maxMentees;
    private long activeMenteesCount;
    private long availableSlots;
    private Set<InterestTagDto> expertiseTags = new HashSet<>();
    private LocalDateTime createdAt;

    public AlumniResponseDto() {
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

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public Integer getGraduationYear() {
        return graduationYear;
    }

    public void setGraduationYear(Integer graduationYear) {
        this.graduationYear = graduationYear;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Integer getMaxMentees() {
        return maxMentees;
    }

    public void setMaxMentees(Integer maxMentees) {
        this.maxMentees = maxMentees;
    }

    public long getActiveMenteesCount() {
        return activeMenteesCount;
    }

    public void setActiveMenteesCount(long activeMenteesCount) {
        this.activeMenteesCount = activeMenteesCount;
    }

    public long getAvailableSlots() {
        return availableSlots;
    }

    public void setAvailableSlots(long availableSlots) {
        this.availableSlots = availableSlots;
    }

    public Set<InterestTagDto> getExpertiseTags() {
        return expertiseTags;
    }

    public void setExpertiseTags(Set<InterestTagDto> expertiseTags) {
        this.expertiseTags = expertiseTags;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
