package com.example.GameWWW.controllers;

import com.example.GameWWW.model.dto.request.GameInfoReq;
import com.example.GameWWW.model.dto.responce.GameInfoResp;
import com.example.GameWWW.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/games")
public class GameController {
    private final GameService gameService;

    @PostMapping
    public GameInfoResp addGame (@RequestBody GameInfoReq req){
        return gameService.addGame(req);
    }

    @GetMapping("/{id}")
    public GameInfoResp getGame (@PathVariable Long id){
        return gameService.getGame(id);
    }

    @PutMapping("/{id}")
    public GameInfoResp updateGame (@PathVariable Long id,@RequestBody GameInfoReq req){
        return gameService.updateGame(id,req);
    }

    @DeleteMapping ("/{id}")
    public void deleteGame(@PathVariable Long id){
        gameService.deleteGame(id);
    }

    @GetMapping
    public List<GameInfoResp> getAllGames (){
        return gameService.getAllGames();
    }

    @PostMapping ("linkGameAndTeam/{gameID}/{teamID}")
    public GameInfoResp linkGameAndTeam (@PathVariable Long gameID, @PathVariable Long teamID){
        return gameService.linkGameAndTeam (gameID, teamID);
    }
    @PostMapping("winnerOfGame/{id}")
    public GameInfoResp getWinnerOfGame(@PathVariable Long id){
        return gameService.defineWinnerTeam(id);
    }

}
