package com.mentorconnect.dto;

import com.mentorconnect.entity.MentorshipPairStatus;
import java.time.LocalDateTime;

public class EngagementReportDto {

    private Long pairId;
    private Long mentorId;
    private String mentorName;
    private String mentorEmail;
    private String mentorCompany;
    private Long studentId;
    private String studentName;
    private String studentEmail;
    private String studentDepartment;
    private MentorshipPairStatus pairStatus;
    private long totalSessions;
    private long completedSessions;
    private long scheduledSessions;
    private long cancelledSessions;
    private long totalCompletedMinutes;
    private double totalCompletedHours;
    private LocalDateTime lastSessionDate;

    public EngagementReportDto() {
    }

    public Long getPairId() {
        return pairId;
    }

    public void setPairId(Long pairId) {
        this.pairId = pairId;
    }

    public Long getMentorId() {
        return mentorId;
    }

    public void setMentorId(Long mentorId) {
        this.mentorId = mentorId;
    }

    public String getMentorName() {
        return mentorName;
    }

    public void setMentorName(String mentorName) {
        this.mentorName = mentorName;
    }

    public String getMentorEmail() {
        return mentorEmail;
    }

    public void setMentorEmail(String mentorEmail) {
        this.mentorEmail = mentorEmail;
    }

    public String getMentorCompany() {
        return mentorCompany;
    }

    public void setMentorCompany(String mentorCompany) {
        this.mentorCompany = mentorCompany;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    public String getStudentDepartment() {
        return studentDepartment;
    }

    public void setStudentDepartment(String studentDepartment) {
        this.studentDepartment = studentDepartment;
    }

    public MentorshipPairStatus getPairStatus() {
        return pairStatus;
    }

    public void setPairStatus(MentorshipPairStatus pairStatus) {
        this.pairStatus = pairStatus;
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

    public long getCancelledSessions() {
        return cancelledSessions;
    }

    public void setCancelledSessions(long cancelledSessions) {
        this.cancelledSessions = cancelledSessions;
    }

    public long getTotalCompletedMinutes() {
        return totalCompletedMinutes;
    }

    public void setTotalCompletedMinutes(long totalCompletedMinutes) {
        this.totalCompletedMinutes = totalCompletedMinutes;
    }

    public double getTotalCompletedHours() {
        return totalCompletedHours;
    }

    public void setTotalCompletedHours(double totalCompletedHours) {
        this.totalCompletedHours = totalCompletedHours;
    }

    public LocalDateTime getLastSessionDate() {
        return lastSessionDate;
    }

    public void setLastSessionDate(LocalDateTime lastSessionDate) {
        this.lastSessionDate = lastSessionDate;
    }
}
