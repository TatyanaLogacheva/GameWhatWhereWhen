package com.example.GameWWW.model.db.repository;

import com.example.GameWWW.model.db.entity.GameTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GameTeamRepository extends JpaRepository<GameTeam, Long> {

    @Query("select sum (ta.point) from TeamAnswer ta where ta.team.id = :teamID")
    Integer getTotalPoints(@Param("teamID") Long teamID);

    @Query("select gt from GameTeam gt where gt.totalPoints =" +
            "(select max (gt.totalPoints) from GameTeam gt where gt.game.id = :gameID)")
    Optional<GameTeam> findMaxPoints(@Param("gameID") Long gameID);
}
