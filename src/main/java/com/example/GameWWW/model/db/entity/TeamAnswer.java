package com.example.GameWWW.model.db.entity;

import com.example.GameWWW.model.enums.Status;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "teams_answers")
public class TeamAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "text_answer")
    private String textAnswer;

    @Column(name = "point")
    private Integer point;

    @Column(name = "appeal")
    private String appeal;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private QuestionInfo question;

    @ManyToOne
    @JsonBackReference(value = "team_answers")
    private Team team;
}