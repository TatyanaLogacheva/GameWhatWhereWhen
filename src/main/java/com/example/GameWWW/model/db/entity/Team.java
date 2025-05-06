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
@Table(name = "teams")
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "team_name")
    private String teamName;


    @Column(name = "country")
    private String country;

    @Column(name = "city")
    private String city;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "team")
    @JsonIgnore
    private List<PlayerTeam> playerTeams;

    @OneToMany
    @JsonManagedReference(value = "team_answers")
    private List<TeamAnswer> teamAnswers;

    @OneToMany(mappedBy = "team", cascade = CascadeType.REFRESH)
    @JsonIgnore
    private List<GameTeam> gameTeams;
}
