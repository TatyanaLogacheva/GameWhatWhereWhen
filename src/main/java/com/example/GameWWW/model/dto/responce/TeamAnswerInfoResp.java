package com.example.GameWWW.model.dto.responce;

import com.example.GameWWW.model.dto.request.TeamAnswerInfoReq;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TeamAnswerInfoResp extends TeamAnswerInfoReq {

    private Long id;
    private TeamInfoResp team;
    private QuestionInfoResp question;
}
