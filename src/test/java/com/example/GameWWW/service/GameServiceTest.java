package com.example.GameWWW.service;

import com.example.GameWWW.exceptions.CommonBackendException;
import com.example.GameWWW.model.db.entity.Game;
import com.example.GameWWW.model.db.entity.GameTeam;
import com.example.GameWWW.model.db.entity.Team;
import com.example.GameWWW.model.db.repository.GameRepository;
import com.example.GameWWW.model.db.repository.GameTeamRepository;
import com.example.GameWWW.model.db.repository.TeamRepository;
import com.example.GameWWW.model.dto.request.GameInfoReq;
import com.example.GameWWW.model.dto.responce.GameInfoResp;
import com.example.GameWWW.model.enums.Status;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class GameServiceTest {
    @InjectMocks
    private GameService gameService;

    @Mock
    private GameRepository gameRepository;
    @Mock
    private GameTeamRepository gameTeamRepository;
    @Mock
    private TeamService teamService;
    @Mock
    private TeamRepository teamRepository;

    @Spy
    private ObjectMapper mapper;

    @Test
    public void addGame() {
        GameInfoReq req = new GameInfoReq();
        req.setGameName("Test");
        req.setAmountOfQuestions(50);

        Game game = new Game();
        game.setId(1L);
        game.setGameName(req.getGameName());
        game.setAmountOfQuestions(req.getAmountOfQuestions());
        when(gameRepository.save(any(Game.class))).thenReturn(game);
        GameInfoResp resp =  gameService.addGame(req);
        assertEquals(game.getId(), resp.getId());
    }

    @Test
    public void getGame() {
        Game game = new Game();
        game.setId(1L);
        game.setGameName("Test");
        when(gameRepository.findById(game.getId())).thenReturn(Optional.of(game));

        GameInfoResp gameResp = gameService.getGame(game.getId());
        assertEquals(game.getGameName(), gameResp.getGameName());
    }

    @Test
    public void getGameFromDB() {
        Game game = new Game();
        game.setId(1L);
        game.setGameName("Test");
        when(gameRepository.findById(game.getId())).thenReturn(Optional.of(game));

        Game gameDB = gameService.getGameFromDB(game.getId());
        assertEquals(game.getGameName(), gameDB.getGameName());
    }

    @Test(expected = CommonBackendException.class)
    public void getGameFromDBNotFound(){
        gameService.getGameFromDB(1L);
    }

    @Test
    public void updateGame() {
        GameInfoReq req = new GameInfoReq();
        req.setGameName("Test");
        req.setAmountOfQuestions(50);

        Game game = new Game();
        game.setId(1L);
        game.setGameName("Test");
        when(gameRepository.findById(game.getId())).thenReturn(Optional.of(game));
        gameService.updateGame(game.getId(), req);
        assertEquals(req.getGameName(), game.getGameName());
        assertEquals(req.getAmountOfQuestions(), game.getAmountOfQuestions());
    }

    @Test
    public void deleteGame() {
        Game game = new Game();
        game.setId(1L);
        GameTeam gameTeam = new GameTeam();
        gameTeam.setId(1L);
        game.setGameTeams(List.of(gameTeam));
        when(gameRepository.findById(game.getId())).thenReturn(Optional.of(game));
        gameService.deleteGame(game.getId());
        verify(gameRepository,times(1)).save(any(Game.class));
        verify(gameTeamRepository,times(1)).deleteById(gameTeam.getId());
        assertEquals(Status.DELETED, game.getStatus());

    }

    @Test
    public void getAllGames() {
        Game game1 = new Game();
        game1.setId(1L);
        game1.setGameName("Test");

        Game game2 = new Game();
        game2.setId(2L);
        game2.setGameName("Game");
        List<Game> gameList = Arrays.asList(game1, game2);
        when(gameRepository.findAll()).thenReturn(List.of(game1, game2));
        List<GameInfoResp> resp = gameService.getAllGames();
        assertEquals(gameList.get(0).getId(), resp.get(0).getId());
        assertEquals(gameList.get(1).getId(), resp.get(1).getId());
    }

    @Test
    public void updateTourList() {
        Game game = new Game();
        game.setId(1L);
        gameService.updateTourList(game);
        verify(gameRepository, times(1)).save(any(Game.class));
    }

    @Test
    public void linkGameAndTeam() {
        Game game = new Game();
        game.setId(1L);
        when(gameRepository.findById(game.getId())).thenReturn(Optional.of(game));

        Team team = new Team();
        team.setId(1L);
        when(teamService.getTeamFromDB(team.getId())).thenReturn(team);

        Team exTeam = new Team();
        exTeam.setId(3L);

        GameTeam gameTeam = new GameTeam();
        gameTeam.setId(1L);
        GameTeam exGameTeam = new GameTeam();
        exGameTeam.setId(2L);
        exGameTeam.setTeam(exTeam);

        List<GameTeam> gameTeamsToGame = new ArrayList<>();
        gameTeamsToGame.add(exGameTeam);
        game.setGameTeams(gameTeamsToGame);
        List<GameTeam> gameTeamsToTeam = new ArrayList<>();
        team.setGameTeams(gameTeamsToTeam);

        when(gameTeamRepository.save(any(GameTeam.class))).thenReturn(gameTeam);
        when(teamRepository.save(any(Team.class))).thenReturn(team);
        GameInfoResp resp = gameService.linkGameAndTeam(game.getId(), team.getId());
        assertEquals(team.getId(), resp.getTeam().getId());
    }

    @Test
    public void defineWinnerTeam() {
        Game game = new Game();
        game.setId(1L);
        when(gameRepository.findById(game.getId())).thenReturn(Optional.of(game));

        GameTeam gameTeam = new GameTeam();
        gameTeam.setId(1L);
        gameTeam.setWinner(false);
        gameTeam.setTotalPoints(10);
        gameTeam.setGame(game);
        Team team = new Team();
        team.setId(1L);
        gameTeam.setTeam(team);
        game.setGameTeams(List.of(gameTeam));
        when(gameTeamRepository.getTotalPoints(gameTeam.getTeam().getId())).thenReturn(gameTeam.getTotalPoints());
        when(gameTeamRepository.findMaxPoints(game.getId())).thenReturn(Optional.of(gameTeam));
        when(gameTeamRepository.save(any(GameTeam.class))).thenReturn(gameTeam);
        GameInfoResp resp = gameService.defineWinnerTeam(game.getId());
        assertTrue(resp.getGameTeam().isWinner());
    }
}