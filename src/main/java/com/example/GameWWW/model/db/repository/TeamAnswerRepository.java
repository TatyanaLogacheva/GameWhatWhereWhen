package com.example.GameWWW.model.db.repository;

import com.example.GameWWW.model.db.entity.TeamAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeamAnswerRepository extends JpaRepository<TeamAnswer, Long> {
    Optional<TeamAnswer> findByQuestion_IdAndTeam_Id(Long questionID, Long teamID);
}
