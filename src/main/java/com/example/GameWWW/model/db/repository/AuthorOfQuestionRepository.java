package com.example.GameWWW.model.db.repository;

import com.example.GameWWW.model.db.entity.AuthorOfQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthorOfQuestionRepository extends JpaRepository<AuthorOfQuestion, Long> {
    Optional<AuthorOfQuestion> findAuthorOfQuestionByEmail(String email);
}
