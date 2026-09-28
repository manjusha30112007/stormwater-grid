package com.mentorconnect.controller;

import com.mentorconnect.dto.EngagementReportDto;
import com.mentorconnect.dto.PlatformSummaryDto;
import com.mentorconnect.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    /**
     * Core Feature 5: View mentorship engagement report (sessions held per mentor pair).
     * Returns detailed breakdown of sessions held, completed, cancelled, and total mentoring hours per pair.
     */
    @GetMapping("/engagement")
    public ResponseEntity<List<EngagementReportDto>> getEngagementReport() {
        return ResponseEntity.ok(reportService.getEngagementReport());
    }

    /**
     * Platform Summary Dashboard metrics.
     */
    @GetMapping("/summary")
    public ResponseEntity<PlatformSummaryDto> getPlatformSummary() {
        return ResponseEntity.ok(reportService.getPlatformSummary());
    }
}
