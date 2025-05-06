package com.example.GameWWW.model.db.repository;

import com.example.GameWWW.model.db.entity.Player;
import com.example.GameWWW.model.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {

    Optional<Player> findByEmail(String email);

    @Query("select p from Player p where p.status <> :status")
    List<Player> findPlayerByStatusNot(@Param("status") Status status);

}
