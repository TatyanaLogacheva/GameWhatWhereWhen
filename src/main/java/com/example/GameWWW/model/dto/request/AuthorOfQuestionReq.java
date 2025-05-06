package com.example.GameWWW.model.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;


@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthorOfQuestionReq {

    @NotNull
    private String authorFirstName;

    @NotNull
    private String authorLastName;

    private String authorMiddleName;

    @NotNull
    private Integer authorAge;

    @NotNull
    private String email;
}
