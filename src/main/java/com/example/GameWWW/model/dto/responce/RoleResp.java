package com.example.GameWWW.model.dto.responce;

import com.example.GameWWW.model.dto.request.RoleReq;
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
public class RoleResp extends RoleReq {
    private Long id;
}
