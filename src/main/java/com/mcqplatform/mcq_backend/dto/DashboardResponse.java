package com.mcqplatform.mcq_backend.dto;
import java.util.List;

import com.mcqplatform.mcq_backend.entity.CandidateAssessment;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardResponse {
    private long totalCandidates;
    private long suspiciousCount;
    private double averageScore;
    private List<CandidateAssessment> candidates;
}