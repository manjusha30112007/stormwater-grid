package com.mentorconnect.dto;

import jakarta.validation.constraints.NotBlank;

public class InterestTagDto {

    private Long id;

    @NotBlank(message = "Tag name cannot be blank")
    private String name;

    public InterestTagDto() {
    }

    public InterestTagDto(Long id, String name) {
        this.id = id;
        this.name = name;
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
}
