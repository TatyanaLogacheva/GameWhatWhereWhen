package com.example.GameWWW.model.db.entity;

import com.example.GameWWW.model.enums.Status;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "games")
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "game_name")
    private String gameName;

    @Column(name = "play_date")
    private String playDate;

    @Column(name = "play_place")
    private String playPlace;

    @Column(name = "amount_of_questions")
    private Integer amountOfQuestions;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany
    @JsonManagedReference(value = "game_tours")
    private List<Tour> tours;

    @OneToMany(mappedBy = "game", cascade = CascadeType.REFRESH)
    @JsonIgnore
    private List<GameTeam> gameTeams;
}
