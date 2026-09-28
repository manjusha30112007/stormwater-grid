package com.mentorconnect.dto;

import java.util.HashSet;
import java.util.Set;

public class MatchSuggestionDto {

    private Long alumniId;
    private String name;
    private String email;
    private String company;
    private String jobTitle;
    private Integer maxMentees;
    private long activeMentees;
    private long availableSlots;
    private int overlapScore;
    private Set<String> overlappingTags = new HashSet<>();
    private Set<String> allExpertiseTags = new HashSet<>();

    public MatchSuggestionDto() {
    }

    public MatchSuggestionDto(Long alumniId, String name, String email, String company, String jobTitle,
                              Integer maxMentees, long activeMentees, long availableSlots,
                              int overlapScore, Set<String> overlappingTags, Set<String> allExpertiseTags) {
        this.alumniId = alumniId;
        this.name = name;
        this.email = email;
        this.company = company;
        this.jobTitle = jobTitle;
        this.maxMentees = maxMentees;
        this.activeMentees = activeMentees;
        this.availableSlots = availableSlots;
        this.overlapScore = overlapScore;
        this.overlappingTags = overlappingTags;
        this.allExpertiseTags = allExpertiseTags;
    }

    public Long getAlumniId() {
        return alumniId;
    }

    public void setAlumniId(Long alumniId) {
        this.alumniId = alumniId;
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

    public Integer getMaxMentees() {
        return maxMentees;
    }

    public void setMaxMentees(Integer maxMentees) {
        this.maxMentees = maxMentees;
    }

    public long getActiveMentees() {
        return activeMentees;
    }

    public void setActiveMentees(long activeMentees) {
        this.activeMentees = activeMentees;
    }

    public long getAvailableSlots() {
        return availableSlots;
    }

    public void setAvailableSlots(long availableSlots) {
        this.availableSlots = availableSlots;
    }

    public int getOverlapScore() {
        return overlapScore;
    }

    public void setOverlapScore(int overlapScore) {
        this.overlapScore = overlapScore;
    }

    public Set<String> getOverlappingTags() {
        return overlappingTags;
    }

    public void setOverlappingTags(Set<String> overlappingTags) {
        this.overlappingTags = overlappingTags;
    }

    public Set<String> getAllExpertiseTags() {
        return allExpertiseTags;
    }

    public void setAllExpertiseTags(Set<String> allExpertiseTags) {
        this.allExpertiseTags = allExpertiseTags;
    }
}
