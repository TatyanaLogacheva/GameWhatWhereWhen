package com.example.GameWWW.model.dto.responce;

import com.example.GameWWW.model.dto.request.AuthorOfQuestionReq;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthorOfQuestionResp extends AuthorOfQuestionReq {

    private Long id;
}
