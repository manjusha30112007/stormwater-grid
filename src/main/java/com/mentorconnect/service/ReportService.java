package com.mentorconnect.service;

import com.mentorconnect.dto.EngagementReportDto;
import com.mentorconnect.dto.PlatformSummaryDto;

import java.util.List;

public interface ReportService {
    List<EngagementReportDto> getEngagementReport();
    PlatformSummaryDto getPlatformSummary();
}
