package com.mentorconnect.dto;

import java.time.LocalDateTime;

public class SessionCompleteDto {

    private String feedback;
    private LocalDateTime completedAt;

    public SessionCompleteDto() {
    }

    public SessionCompleteDto(String feedback) {
        this.feedback = feedback;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
}
