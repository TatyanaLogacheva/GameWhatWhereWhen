package com.example.GameWWW.model.db.entity;

import com.example.GameWWW.model.enums.Status;
import com.fasterxml.jackson.annotation.JsonBackReference;
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
@Table(name = "tours")
public class Tour {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tour_number")
    private Integer tourNumber;

    @Column(name = "amount_of_questions_in_tour")
    private Integer amountOfQuestionsInTour;

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
    @JsonManagedReference(value = "tour_questions")
    private List<QuestionInfo> questions;

    @ManyToOne
    @JsonBackReference(value = "game_tours")
    private Game game;
}
