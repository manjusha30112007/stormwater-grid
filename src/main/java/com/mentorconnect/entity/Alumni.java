package com.mentorconnect.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "alumni")
public class Alumni {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Alumni name is required")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Column(nullable = false, unique = true)
    private String email;

    private Integer graduationYear;

    private String company;

    private String jobTitle;

    @Column(length = 1000)
    private String bio;

    /**
     * Declared maximum concurrent mentee limit.
     * Enforced in business logic so active matches cannot exceed this value.
     */
    @NotNull(message = "Max concurrent mentees limit is required")
    @Min(value = 1, message = "Max mentees limit must be at least 1")
    @Column(nullable = false)
    private Integer maxMentees = 2;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "alumni_expertise_tags",
            joinColumns = @JoinColumn(name = "alumni_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<InterestTag> expertiseTags = new HashSet<>();

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Alumni() {
    }

    public Alumni(String name, String email, String company, String jobTitle, Integer graduationYear, Integer maxMentees) {
        this.name = name;
        this.email = email;
        this.company = company;
        this.jobTitle = jobTitle;
        this.graduationYear = graduationYear;
        this.maxMentees = maxMentees != null ? maxMentees : 2;
        this.createdAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (maxMentees == null) {
            maxMentees = 2;
        }
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

    public Integer getGraduationYear() {
        return graduationYear;
    }

    public void setGraduationYear(Integer graduationYear) {
        this.graduationYear = graduationYear;
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

    public Set<InterestTag> getExpertiseTags() {
        return expertiseTags;
    }

    public void setExpertiseTags(Set<InterestTag> expertiseTags) {
        this.expertiseTags = expertiseTags != null ? expertiseTags : new HashSet<>();
    }

    public void addExpertiseTag(InterestTag tag) {
        if (tag != null) {
            this.expertiseTags.add(tag);
        }
    }

    public void removeExpertiseTag(InterestTag tag) {
        if (tag != null) {
            this.expertiseTags.remove(tag);
        }
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
