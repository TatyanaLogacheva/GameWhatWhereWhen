package com.example.GameWWW.controllers;

import com.example.GameWWW.model.dto.request.PlayerInfoReq;
import com.example.GameWWW.model.dto.responce.PlayerInfoResp;
import com.example.GameWWW.model.dto.responce.TeamInfoResp;
import com.example.GameWWW.service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/players")
@RequiredArgsConstructor
public class PlayerController {
    private final PlayerService playerService;

    @PostMapping
    public PlayerInfoResp addPlayer (@RequestBody @Valid PlayerInfoReq req){
        return playerService.addPlayer(req);
    }

    @GetMapping("/{id}")
    public PlayerInfoResp getPlayer (@PathVariable Long id){
        return playerService.getPlayer(id);
    }

    @PutMapping("/{id}")
    public PlayerInfoResp updatePlayer(@PathVariable Long id, @RequestBody PlayerInfoReq req){
        return  playerService.updatePlayer(id, req);
    }

    @DeleteMapping ("/{id}")
    public void deletePlayer(@PathVariable Long id){
        playerService.deletePlayer(id);
    }

    @GetMapping
    public List<PlayerInfoResp> getAllPlayers (){
        return playerService.getAllPlayers();
    }

     @GetMapping("/{id}/teams")
    public List<TeamInfoResp> getMyTeams(@PathVariable Long id){
        return playerService.getMyTeams(id);
     }


}
