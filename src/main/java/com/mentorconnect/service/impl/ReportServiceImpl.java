package com.mentorconnect.service.impl;

import com.mentorconnect.dto.EngagementReportDto;
import com.mentorconnect.dto.PlatformSummaryDto;
import com.mentorconnect.entity.MentorshipPair;
import com.mentorconnect.entity.MentorshipPairStatus;
import com.mentorconnect.entity.Session;
import com.mentorconnect.entity.SessionStatus;
import com.mentorconnect.repository.AlumniRepository;
import com.mentorconnect.repository.MentorshipPairRepository;
import com.mentorconnect.repository.SessionRepository;
import com.mentorconnect.repository.StudentRepository;
import com.mentorconnect.service.ReportService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {

    private final MentorshipPairRepository pairRepository;
    private final SessionRepository sessionRepository;
    private final AlumniRepository alumniRepository;
    private final StudentRepository studentRepository;

    public ReportServiceImpl(MentorshipPairRepository pairRepository,
                             SessionRepository sessionRepository,
                             AlumniRepository alumniRepository,
                             StudentRepository studentRepository) {
        this.pairRepository = pairRepository;
        this.sessionRepository = sessionRepository;
        this.alumniRepository = alumniRepository;
        this.studentRepository = studentRepository;
    }

    @Override
    public List<EngagementReportDto> getEngagementReport() {
        List<MentorshipPair> pairs = pairRepository.findAll();
        List<EngagementReportDto> report = new ArrayList<>();

        for (MentorshipPair pair : pairs) {
            EngagementReportDto dto = new EngagementReportDto();
            dto.setPairId(pair.getId());
            dto.setMentorId(pair.getAlumni().getId());
            dto.setMentorName(pair.getAlumni().getName());
            dto.setMentorEmail(pair.getAlumni().getEmail());
            dto.setMentorCompany(pair.getAlumni().getCompany());

            dto.setStudentId(pair.getStudent().getId());
            dto.setStudentName(pair.getStudent().getName());
            dto.setStudentEmail(pair.getStudent().getEmail());
            dto.setStudentDepartment(pair.getStudent().getDepartment());

            dto.setPairStatus(pair.getStatus());

            List<Session> sessions = sessionRepository.findByMentorshipPairIdOrderByScheduledTimeDesc(pair.getId());
            long total = sessions.size();
            long completed = sessions.stream().filter(s -> s.getStatus() == SessionStatus.COMPLETED).count();
            long scheduled = sessions.stream().filter(s -> s.getStatus() == SessionStatus.SCHEDULED).count();
            long cancelled = sessions.stream().filter(s -> s.getStatus() == SessionStatus.CANCELLED).count();

            long totalMinutes = sessionRepository.getTotalCompletedMinutesForPair(pair.getId());

            dto.setTotalSessions(total);
            dto.setCompletedSessions(completed);
            dto.setScheduledSessions(scheduled);
            dto.setCancelledSessions(cancelled);
            dto.setTotalCompletedMinutes(totalMinutes);
            dto.setTotalCompletedHours(Math.round((totalMinutes / 60.0) * 10.0) / 10.0);

            if (!sessions.isEmpty()) {
                dto.setLastSessionDate(sessions.get(0).getScheduledTime());
            }

            report.add(dto);
        }

        // Sort by completed sessions descending, then total sessions descending
        report.sort(Comparator
                .comparingLong(EngagementReportDto::getCompletedSessions).reversed()
                .thenComparingLong(EngagementReportDto::getTotalSessions).reversed());

        return report;
    }

    @Override
    public PlatformSummaryDto getPlatformSummary() {
        PlatformSummaryDto summary = new PlatformSummaryDto();
        summary.setTotalAlumni(alumniRepository.count());
        summary.setTotalStudents(studentRepository.count());
        summary.setActivePairs(pairRepository.countByStatus(MentorshipPairStatus.ACTIVE));
        summary.setCompletedPairs(pairRepository.countByStatus(MentorshipPairStatus.COMPLETED));
        summary.setTotalSessions(sessionRepository.count());
        summary.setCompletedSessions(sessionRepository.countByStatus(SessionStatus.COMPLETED));
        summary.setScheduledSessions(sessionRepository.countByStatus(SessionStatus.SCHEDULED));

        Long totalMinutes = sessionRepository.getTotalPlatformCompletedMinutes();
        summary.setTotalMentoringHours(Math.round((totalMinutes / 60.0) * 10.0) / 10.0);

        return summary;
    }
}
