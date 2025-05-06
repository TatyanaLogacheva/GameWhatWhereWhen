package com.example.GameWWW.model.db.repository;

import com.example.GameWWW.model.db.entity.Team;
import com.example.GameWWW.model.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

    Optional<Team> findTeamByTeamName(String teamName);

    @Query("select t from Team t where t.status <> :status")
    List<Team> findTeamsByStatusNot(@Param("status") Status status);

}
