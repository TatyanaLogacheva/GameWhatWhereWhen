package com.example.GameWWW.model.dto.responce;

import com.example.GameWWW.model.dto.request.UserReq;
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
public class UserResp extends UserReq {
    private Long id;
    private TeamInfoResp teamInfoResp;
}
