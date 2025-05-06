package com.example.GameWWW.model.db.repository;

import com.example.GameWWW.model.db.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
    Optional<Game> findGameByPlayPlaceAndPlayDate(String place, Date date);
}
