package com.mentorconnect.dto;

public class PlatformSummaryDto {

    private long totalAlumni;
    private long totalStudents;
    private long activePairs;
    private long completedPairs;
    private long totalSessions;
    private long completedSessions;
    private long scheduledSessions;
    private double totalMentoringHours;

    public PlatformSummaryDto() {
    }

    public long getTotalAlumni() {
        return totalAlumni;
    }

    public void setTotalAlumni(long totalAlumni) {
        this.totalAlumni = totalAlumni;
    }

    public long getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(long totalStudents) {
        this.totalStudents = totalStudents;
    }

    public long getActivePairs() {
        return activePairs;
    }

    public void setActivePairs(long activePairs) {
        this.activePairs = activePairs;
    }

    public long getCompletedPairs() {
        return completedPairs;
    }

    public void setCompletedPairs(long completedPairs) {
        this.completedPairs = completedPairs;
    }

    public long getTotalSessions() {
        return totalSessions;
    }

    public void setTotalSessions(long totalSessions) {
        this.totalSessions = totalSessions;
    }

    public long getCompletedSessions() {
        return completedSessions;
    }

    public void setCompletedSessions(long completedSessions) {
        this.completedSessions = completedSessions;
    }

    public long getScheduledSessions() {
        return scheduledSessions;
    }

    public void setScheduledSessions(long scheduledSessions) {
        this.scheduledSessions = scheduledSessions;
    }

    public double getTotalMentoringHours() {
        return totalMentoringHours;
    }

    public void setTotalMentoringHours(double totalMentoringHours) {
        this.totalMentoringHours = totalMentoringHours;
    }
}
