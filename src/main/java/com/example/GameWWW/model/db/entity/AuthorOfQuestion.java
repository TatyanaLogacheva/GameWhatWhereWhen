package com.example.GameWWW.model.db.entity;

import com.example.GameWWW.model.enums.Status;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "author_of_question")
public class AuthorOfQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "authorFirstName")
    private String authorFirstName;

    @Column(name = "authorLastName")
    private String authorLastName;

    @Column(name = "authorMiddleName")
    private String authorMiddleName;

    @Column(name = "authorAge")
    private Integer authorAge;

    @Column(name = "email")
    private String email;

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
    @JsonManagedReference(value = "author_question")
    private List<QuestionInfo> questions;
}
