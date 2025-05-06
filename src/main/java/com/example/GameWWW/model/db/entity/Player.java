package com.example.GameWWW.model.db.entity;

import com.example.GameWWW.model.enums.Gender;
import com.example.GameWWW.model.enums.Status;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "players")
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "date_of_birthday")
    private String dateOfBirthday;

    @Column(name = "telephon_Num")
    private String telephonNum;

    @Column(name = "email")
    private String email;

    @Column(name = "place_of_work_or_study")
    private String placeOfWorkOrStudy;

    @Column(name = "additional_information")
    private String additionalInformation;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "player", cascade = CascadeType.REFRESH)
    @JsonIgnore
    private List<PlayerTeam> playerTeams;


}
