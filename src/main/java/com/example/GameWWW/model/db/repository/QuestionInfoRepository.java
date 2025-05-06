package com.example.GameWWW.model.db.repository;

import com.example.GameWWW.model.db.entity.QuestionInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuestionInfoRepository extends JpaRepository<QuestionInfo, Long> {
    Optional<QuestionInfo> findQuestionInfoByText(String text);
}
