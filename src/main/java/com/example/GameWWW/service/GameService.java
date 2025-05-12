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
import com.example.GameWWW.model.dto.responce.GameTeamResp;
import com.example.GameWWW.model.dto.responce.TeamInfoResp;
import com.example.GameWWW.model.enums.Status;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameService {
    private final ObjectMapper objectMapper;
    private final GameRepository gameRepository;
    private final TeamService teamService;
    private final GameTeamRepository gameTeamRepository;
    private final TeamRepository teamRepository;

    public GameInfoResp addGame(GameInfoReq req) {
        gameRepository.findGameByPlayPlaceAndPlayDate(req.getPlayPlace(), req.getPlayDate())
                .ifPresent(game -> {
                    throw new CommonBackendException("Game already existed", HttpStatus.CONFLICT);
                });

        Game game = objectMapper.convertValue(req, Game.class);
        game.setStatus(Status.CREATED);
        Game save = gameRepository.save(game);
        return objectMapper.convertValue(save, GameInfoResp.class);
    }

    public GameInfoResp getGame(Long id) {
        Game game = getGameFromDB(id);
        return objectMapper.convertValue(game, GameInfoResp.class);
    }

    public Game getGameFromDB(Long id) {
        Optional<Game> optionalGame = gameRepository.findById(id);
        final String errorMsg = String.format("Game with id %s is not found", id);
        return optionalGame.orElseThrow(() -> new CommonBackendException(errorMsg, HttpStatus.NOT_FOUND));
    }

    public GameInfoResp updateGame(Long id, GameInfoReq req) {
        Game gameFromDB = getGameFromDB(id);
        Game gameReq = objectMapper.convertValue(req, Game.class);

        gameFromDB.setGameName(gameFromDB.getGameName() == null ? gameFromDB.getGameName() : gameReq.getGameName());
        gameFromDB.setPlayDate(gameReq.getPlayDate() == null ? gameFromDB.getPlayDate() : gameReq.getPlayDate());
        gameFromDB.setPlayPlace(gameReq.getPlayPlace() == null ? gameFromDB.getPlayPlace() : gameReq.getPlayPlace());
        gameFromDB.setAmountOfQuestions(gameReq.getAmountOfQuestions() == null ? gameFromDB.getAmountOfQuestions() : gameReq.getAmountOfQuestions());

        gameFromDB.setStatus(Status.UPDATED);
        gameFromDB = gameRepository.save(gameFromDB);

        return objectMapper.convertValue(gameFromDB, GameInfoResp.class);
    }

    public void deleteGame(Long id) {
        Game game = getGameFromDB(id);
        game.setStatus(Status.DELETED);
        gameRepository.save(game);
        game.getGameTeams().forEach(gameTeam -> gameTeamRepository.deleteById(gameTeam.getId()));
    }

    public List<GameInfoResp> getAllGames() {
        return gameRepository.findAll().stream()
                .map(game -> objectMapper.convertValue(game, GameInfoResp.class))
                .collect(Collectors.toList());
    }

    public Game updateTourList(Game updateGame) {
        return gameRepository.save(updateGame);
    }

    public GameInfoResp linkGameAndTeam(Long gameID, Long teamID) {
        Game gameFromDB = getGameFromDB(gameID);
        Team teamFromDB = teamService.getTeamFromDB(teamID);

        if (gameFromDB.getId() == null || teamFromDB.getId() == null) {
            throw new CommonBackendException("Game or Team is not found", HttpStatus.NOT_FOUND);
        }
        GameTeam gameTeam = new GameTeam();

        List<GameTeam> gameTeamsToGame = gameFromDB.getGameTeams();
        GameTeam existedGameTeam = gameTeamsToGame.stream().filter(gtToGame -> gtToGame
                .getTeam().getId().equals(teamID)).findFirst().orElse(null);

        if (existedGameTeam != null) {
            throw new CommonBackendException("Team is already added", HttpStatus.CONFLICT);
        }
        gameTeamsToGame.add(gameTeam);
        gameTeam.setGame(gameFromDB);

        List<GameTeam> gameTeamsToTeam = teamFromDB.getGameTeams();
        gameTeamsToTeam.add(gameTeam);
        gameTeam.setTeam(teamFromDB);

        gameRepository.save(gameFromDB);
        gameTeamRepository.save(gameTeam);
        teamRepository.save(teamFromDB);

        GameInfoResp gameInfoResp = objectMapper.convertValue(gameFromDB, GameInfoResp.class);
        TeamInfoResp teamInfoResp = objectMapper.convertValue(teamFromDB, TeamInfoResp.class);
        gameInfoResp.setTeam(teamInfoResp);

        return gameInfoResp;
    }

    public GameInfoResp defineWinnerTeam(Long id) {
        Game game = getGameFromDB(id);
        game.getGameTeams().forEach(gt -> {
            if (gt.getWinner()) {
                gt.setWinner(false);
            }
            gt.setTotalPoints(gameTeamRepository.getTotalPoints(gt.getTeam().getId()));
        });
        Optional<GameTeam> optionalGameTeam = gameTeamRepository.findMaxPoints(id);
        GameTeam gameTeamWinner = optionalGameTeam.get();
        gameTeamWinner.setWinner(true);
        gameTeamWinner = gameTeamRepository.save(gameTeamWinner);
        Team winnerTeam = gameTeamWinner.getTeam();
        GameInfoResp gameInfoResp = objectMapper.convertValue(game, GameInfoResp.class);
        TeamInfoResp teamInfoResp = objectMapper.convertValue(winnerTeam, TeamInfoResp.class);
        GameTeamResp gameTeamResp = objectMapper.convertValue(gameTeamWinner, GameTeamResp.class);
        gameInfoResp.setTeam(teamInfoResp);
        gameInfoResp.setGameTeam(gameTeamResp);
        return gameInfoResp;
    }
}
