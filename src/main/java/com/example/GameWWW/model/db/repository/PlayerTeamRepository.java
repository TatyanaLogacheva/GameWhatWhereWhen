package com.example.GameWWW.model.db.repository;

import com.example.GameWWW.model.db.entity.PlayerTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerTeamRepository extends JpaRepository<PlayerTeam, Long> {

    PlayerTeam findPlayerTeamByTeam_IdAndPlayer_Id(Long teamID, Long playerID);
}
