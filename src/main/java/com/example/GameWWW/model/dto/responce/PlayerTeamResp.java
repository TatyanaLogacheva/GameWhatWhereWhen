package com.example.GameWWW.model.dto.responce;

import com.example.GameWWW.model.dto.request.PlayerTeamReq;
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
public class PlayerTeamResp extends PlayerTeamReq {
    private Long id;
}
