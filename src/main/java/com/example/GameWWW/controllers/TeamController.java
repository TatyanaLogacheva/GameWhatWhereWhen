package com.example.GameWWW.controllers;

import com.example.GameWWW.model.dto.request.PlayerTeamReq;
import com.example.GameWWW.model.dto.request.TeamInfoReq;
import com.example.GameWWW.model.dto.responce.PlayerInfoResp;
import com.example.GameWWW.model.dto.responce.TeamInfoResp;
import com.example.GameWWW.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/teams")
public class TeamController {
    private final TeamService teamService;

    @PostMapping
    public TeamInfoResp addTeam (@RequestBody TeamInfoReq req){
        return teamService.addTeam(req);
    }

    @GetMapping("/{id}")
    public TeamInfoResp getTeam (@PathVariable Long id){
        return teamService.getTeam(id);
    }

    @PutMapping("/{id}")
    public TeamInfoResp updateTeam (@PathVariable Long id, @RequestBody TeamInfoReq req){
        return teamService.updateTeam(id, req);
    }

    @DeleteMapping ("/{id}")
    public void deleteTeam(@PathVariable Long id){
        teamService.deleteTeam(id);
    }

    @GetMapping
    public List<TeamInfoResp> getAllTeams(){
        return teamService.getAllTeams();
    }

    @GetMapping("/exists")
    public List<TeamInfoResp> getAllExistsTeams(){
        return teamService.getAllExistsTeams();
    }

    @PostMapping("linkTeamAndPlayer/{teamID}/{playerID}")
    public TeamInfoResp linkTeamAndPlayer (@PathVariable Long teamID, @PathVariable Long playerID,
                                           @RequestBody PlayerTeamReq req){
        return teamService.linkTeamAndPlayer(teamID, playerID, req);
    }
    @PostMapping("changeCapitan/{teamID}/{playerID}")
    public void changeCapitan (@PathVariable Long teamID, @PathVariable Long playerID){
        teamService.changeCapitan(teamID,playerID);
    }

    @GetMapping("/allPlayers/{id}")
    public List<PlayerInfoResp> getAllPlayers(@PathVariable Long id){
        return teamService.getAllPlayers(id);
    }

    @DeleteMapping("/deletePlayer/{teamID}/{playerID}")
    public void deletePlayer (@PathVariable Long teamID, @PathVariable Long playerID){
        teamService.deletePlayer(teamID,playerID);
    }
}
