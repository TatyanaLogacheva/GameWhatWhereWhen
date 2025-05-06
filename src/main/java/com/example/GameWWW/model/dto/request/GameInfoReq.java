package com.example.GameWWW.model.dto.request;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GameInfoReq {

    @NotNull
    private String gameName;

    @NotNull
    @JsonFormat(pattern = "dd.MM.yyyy HH:mm")
    private Date playDate;

    @NotNull
    private String playPlace;

    private Integer amountOfQuestions;

}
