package com.example.GameWWW.model.db.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "game_team")
public class GameTeam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "game_id")
    private Game game;

    @ManyToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "team_id")
    private Team team;

    @Column(name = "winner")
    private Boolean winner;

    @Column(name = "total_points")
    private Integer totalPoints;
}
