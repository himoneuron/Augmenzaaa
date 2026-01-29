package com.mcqplatform.mcq_backend.service;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Import this

import com.mcqplatform.mcq_backend.dto.AssessmentResultResponse;
import com.mcqplatform.mcq_backend.dto.AssessmentSubmissionRequest;
import com.mcqplatform.mcq_backend.dto.DashboardResponse;
import com.mcqplatform.mcq_backend.entity.CandidateAssessment;
import com.mcqplatform.mcq_backend.entity.Question;
import com.mcqplatform.mcq_backend.repository.AssessmentRepository;
import com.mcqplatform.mcq_backend.repository.QuestionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AssessmentService {
private final AssessmentRepository assessmentRepo;
    private final QuestionRepository questionRepo;

    // 1. GET Questions for the User (New Feature)
    public List<Question> getRandomQuestions(int count) {
        List<Question> questions = questionRepo.findRandomQuestions(count);
        // Security: Remove correctOptionKey before sending to frontend!
        questions.forEach(q -> q.setCorrectOptionKey(null)); 
        return questions;
    }

    // 2. Submit & Grade (Real Logic)
    @Transactional
    public AssessmentResultResponse calculateAndSave(AssessmentSubmissionRequest request) {
        
        // Fetch the actual questions from DB based on the IDs the user answered
        List<Question> dbQuestions = questionRepo.findAllById(request.getAnswers().keySet());
        
        int correctCount = 0;
        int totalAttempted = dbQuestions.size();

        for (Question q : dbQuestions) {
            String userAnswer = request.getAnswers().get(q.getId());
            if (userAnswer != null && userAnswer.equals(q.getCorrectOptionKey())) {
                correctCount++;
            }
        }

        double score = totalAttempted > 0 ? ((double) correctCount / totalAttempted) * 100 : 0.0;

        // Save to DB
        CandidateAssessment entity = CandidateAssessment.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .contactNumber(request.getContactNumber())
                .roleApplied("Backend Developer")
                .score(score)
                .totalMarks(correctCount)
                .totalQuestions(totalAttempted)
                .isSuspicious(request.getTabSwitchCount() > 2)
                .tabSwitchCount(request.getTabSwitchCount())
                .build();
        
        assessmentRepo.save(entity);

        return AssessmentResultResponse.builder()
                .candidateId(entity.getId().toString())
                .status(score >= 60 ? "PASSED" : "FAILED")
                .scorePercentage(score)
                .build();
    }

    // Add this to AssessmentService.java

        public DashboardResponse getDashboardData() {
        List<CandidateAssessment> allAssessments = assessmentRepo.findAll();
    
        long total = allAssessments.size();
        long suspicious = allAssessments.stream().filter(CandidateAssessment::isSuspicious).count();
        double avgScore = allAssessments.stream()
            .mapToDouble(CandidateAssessment::getScore)
            .average()
            .orElse(0.0);
            return DashboardResponse.builder()
            .totalCandidates(total)
            .suspiciousCount(suspicious)
            .averageScore(Math.round(avgScore * 100.0) / 100.0) // Round to 2 decimals
            .candidates(allAssessments)
            .build();
}
}