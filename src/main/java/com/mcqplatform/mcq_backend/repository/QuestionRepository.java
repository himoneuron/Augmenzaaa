package com.mcqplatform.mcq_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mcqplatform.mcq_backend.entity.Question;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

   // Fetch random questions for the test (H2 Syntax: RAND(), Postgres: RANDOM())
    // We use a safe trick for H2 (Development)
    @Query(value = "SELECT * FROM questions ORDER BY RANDOM() LIMIT :limit", nativeQuery = true)
    List<Question> findRandomQuestions(@Param("limit") int limit);


    
}