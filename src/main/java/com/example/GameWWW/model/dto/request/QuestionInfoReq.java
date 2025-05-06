package com.example.GameWWW.model.dto.request;

import com.example.GameWWW.model.enums.QuestionType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class QuestionInfoReq {

    @NotBlank
    private String text;

    @NotBlank
    private String answer;

    @NotBlank
    private String infoSource;

    @NotNull
    private QuestionType type;

}
