package com.mcqplatform.mcq_backend.controller;


import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mcqplatform.mcq_backend.dto.AssessmentResultResponse;
import com.mcqplatform.mcq_backend.dto.AssessmentSubmissionRequest; // Make sure you have this DTO!
import com.mcqplatform.mcq_backend.dto.DashboardResponse;
import com.mcqplatform.mcq_backend.entity.Question;
import com.mcqplatform.mcq_backend.service.AssessmentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/assessment")
@CrossOrigin(origins = "*") 
@RequiredArgsConstructor
public class AssessmentController {

    private final AssessmentService service;
// NEW ENDPOINT: Get Questions
    @GetMapping("/start")
    public ResponseEntity<List<Question>> startAssessment() {
        // Fetch 3 random questions (you can change this to 30)
        return ResponseEntity.ok(service.getRandomQuestions(3));
    }

    @PostMapping("/submit")
    public ResponseEntity<AssessmentResultResponse> submitTest(@Valid @RequestBody AssessmentSubmissionRequest request) {
        AssessmentResultResponse result = service.calculateAndSave(request);
        return ResponseEntity.ok(result);
    }
    // Add this to AssessmentController.java

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardResponse> getDashboard() {
    return ResponseEntity.ok(service.getDashboardData());
}
}