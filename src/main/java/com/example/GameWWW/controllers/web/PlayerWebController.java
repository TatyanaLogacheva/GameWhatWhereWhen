package com.example.GameWWW.controllers.web;

import com.example.GameWWW.model.dto.request.PlayerInfoReq;
import com.example.GameWWW.model.dto.responce.PlayerInfoResp;
import com.example.GameWWW.service.PlayerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class PlayerWebController {
    private final PlayerService playerService;
    private final ObjectMapper objectMapper;

    @GetMapping("/allPlayers")
    public String getAllPlayers(Model model){
        List<PlayerInfoResp> allPlayers = playerService.getAllExistsPlayers();
        model.addAttribute("allPlayers", allPlayers);
        return "players/all_players";
    }

    @GetMapping("/allPlayers/new")
    public String newPlayer(Model model){
        model.addAttribute("player", new PlayerInfoReq());
        return "players/player_form";
    }

    @PostMapping(path = "/allPlayers/save", consumes = "application/x-www-form-urlencoded")
    public String saveNewPlayer (PlayerInfoReq playerInfoReq){
        playerService.addPlayer(playerInfoReq);
        return "redirect:/allPlayers";
    }

    @GetMapping("/allPlayers/{id}")
    public String getPlayer (@PathVariable Long id, Model model){
        PlayerInfoResp player = playerService.getPlayer(id);
        model.addAttribute("player", player);
        return "players/edit_player";
    }
    @PostMapping (path = "/allPlayers/saveEdit", consumes = "application/x-www-form-urlencoded")
    public String editPlayer (PlayerInfoResp playerInfoResp) {
        Long playerId = playerInfoResp.getId();
        PlayerInfoReq playerInfoReq = objectMapper.convertValue(playerInfoResp, PlayerInfoReq.class);
        playerService.updatePlayer(playerId,playerInfoReq);
        return "redirect:/allPlayers";
    }
    @GetMapping("/allPlayers/delete/{id}")
    public String deletePlayer(@PathVariable Long id){
        playerService.deletePlayer(id);
        return "redirect:/allPlayers";
    }
}
