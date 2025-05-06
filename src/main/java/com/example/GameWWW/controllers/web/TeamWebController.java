package com.example.GameWWW.controllers.web;

import com.example.GameWWW.model.db.entity.Team;
import com.example.GameWWW.model.dto.request.PlayerTeamReq;
import com.example.GameWWW.model.dto.request.TeamInfoReq;
import com.example.GameWWW.model.dto.responce.PlayerInfoResp;
import com.example.GameWWW.model.dto.responce.TeamInfoResp;
import com.example.GameWWW.service.PlayerService;
import com.example.GameWWW.service.TeamService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class TeamWebController {
    private final TeamService teamService;
    private final ObjectMapper objectMapper;
    private final PlayerService playerService;


    @GetMapping("/allTeams")
    public String getAllTeams(Model model){
        List<TeamInfoResp> allTeams = teamService.getAllExistsTeams();
        model.addAttribute("allTeams", allTeams);
        return "teams/teams";
    }
    @GetMapping("/allTeams/new")
    public String newTeam(Model model){
        model.addAttribute("team", new TeamInfoReq());
        return "teams/team_form";
    }
    @PostMapping (path = "/allTeams/save", consumes = "application/x-www-form-urlencoded")
    public String saveNewTeam (TeamInfoReq teamInfoReq){
        teamService.addTeam(teamInfoReq);
        return "redirect:/allTeams";
    }
    @GetMapping("/allTeams/{id}")
    public String getTeam (@PathVariable Long id, Model model){
        TeamInfoResp team = teamService.getTeam(id);
        model.addAttribute("team", team);
        return "teams/edit_team";
    }
    @PostMapping (path = "/allTeams/saveEdit", consumes = "application/x-www-form-urlencoded")
    public String editTeam (TeamInfoResp teamInfoResp) {
        Long teamId = teamInfoResp.getId();
        TeamInfoReq teamInfoReq = objectMapper.convertValue(teamInfoResp, TeamInfoReq.class);
        teamService.updateTeam(teamId,teamInfoReq);
        return "redirect:/allTeams";
    }
    @GetMapping("/allTeams/delete/{id}")
    public String deleteTeam(@PathVariable Long id){
        teamService.deleteTeam(id);
        return "redirect:/allTeams";
    }

    @GetMapping("/allTeams/{id}/addPlayer")
    public String addPlayer(@PathVariable Long id, Model model){
        TeamInfoResp team = teamService.getTeam(id);
        List<PlayerInfoResp> allPlayers = playerService.getAllExistsPlayers();
        model.addAttribute("team", team);
        model.addAttribute("allPlayers", allPlayers);
        model.addAttribute("playerTeam", new PlayerTeamReq());
        return "teams/add_player";
    }

    @PostMapping(path="/allTeams/{teamId}/{playerId}/saveAddPlayer", consumes = "application/x-www-form-urlencoded" )
    public void saveAddPlayer (@PathVariable Long teamId, @PathVariable Long playerId, PlayerTeamReq playerTeamReq){
        teamService.linkTeamAndPlayer(teamId, playerId, playerTeamReq);
    }

    @GetMapping("/allTeams/{id}/getOurPlayers")
    public String getOurPlayers (@PathVariable Long id, Model model){
        Team team = teamService.getTeamFromDB(id);
        List<PlayerInfoResp> ourPlayers = teamService.getAllPlayers(id);
        model.addAttribute("ourPlayers",ourPlayers);
        model.addAttribute("team", team);
        return "teams/our_players";
    }

}
